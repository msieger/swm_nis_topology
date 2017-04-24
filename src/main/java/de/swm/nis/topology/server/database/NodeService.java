package de.swm.nis.topology.server.database;

import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NodeService {

    private static final String PUBLIC = "public";

    public enum ExpandBehavior {ALWAYS, NEVER, IF_OPEN}

    @Autowired
    private EdgeMapper edgeMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private StringMapper stringMapper;

    @Autowired
    private JdbcTemplate templ;

    @Transactional
    public Set<Node> getNodes(String network, RWO rwo) {
        Schema.set(templ, network);
        return new HashSet<>(templ.query("select node_id from connection where rwo_id = ? and rwo_code = ? and app_code = ?",
                new Object[]{rwo.getId(), rwo.getCode(), rwo.getApp()}, nodeMapper));
    }

    private static String behaviorFunction(ExpandBehavior b) {
        switch (b) {
            case ALWAYS:
                return "";
            case NEVER:
                return "is_closeable";
            case IF_OPEN:
                return "is_closed";
            default:
                throw new RuntimeException("Case not implemented");
        }
    }

    @Transactional
    public Set<Edge> getNeighbors(String network, Node node, ExpandBehavior behavior) {
        Schema.set(templ, network, PUBLIC);
        String behSql = behaviorFunction(behavior);
        if(!behSql.isEmpty()) {
            behSql = " and not " + behSql + "(source)";
        }
        String sql = "select source, target, geom, ST_Length(geom) length from neighbor where source = ?"
                + behSql;
        return new HashSet<>(templ.query(sql, new Object[] { node.getId()}, edgeMapper));
    }

    @Transactional
    public Set<Node> filter(String network, Set<Node> nodes, ExpandBehavior behavior) {
        Schema.set(templ, network);
        if(behavior == ExpandBehavior.ALWAYS) {
            return new HashSet<>(nodes);
        }
        String behSql = behaviorFunction(behavior);
        behSql = behSql + "(node_id)";
        String array = Util.pgArray(nodes.stream().map( n -> Long.toString(n.getId())).collect(Collectors.toList()));
        List<Node> list = templ.query("select node_id from (select unnest(?::int8[]) node_id) s where " + behSql, new Object[]{array}, nodeMapper);
        return new HashSet<>(list);
    }

}
