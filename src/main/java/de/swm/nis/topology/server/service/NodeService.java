package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NodeService {

    public enum ExpandBehavior {ALWAYS, NEVER, IF_OPEN}

    @Autowired
    private NodeMapper nodeMapper;

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

    public Set<Node> getNeighbors(Node node, ExpandBehavior behavior) {
        String behSql = behaviorFunction(behavior);
        if(!behSql.isEmpty()) {
            behSql = " and " + behSql;
        }
        behSql = behSql + "(c1.node_id)";
        String sql =
                "select c1.rwo_id, c1.rwo_code, c1.app_code, max(c2.point_idx)" +
                        "from connection c1" +
                        "join connection c2 on c1.rwo_id = c2.rwo_id and c1.rwo_code = c2.rwo_code and c1.app_code = c2.app_code" +
                        "where c1.node_id = ? and c2.point_idx < c1.point_idx" +
                        behSql +
                        "group by c1.rwo_id, c1.rwo_code, c1.app_code" +
                        "union" +
                        "select c1.rwo_id, c1.rwo_code, c1.app_code, min(c2.point_idx)" +
                        "from connection c1" +
                        "join connection c2 on c1.rwo_id = c2.rwo_id and c1.rwo_code = c2.rwo_code and c1.app_code = c2.app_code" +
                        "where c1.node_id = ? and c2.point_idx > c1.point_idx" +
                        behSql +
                        "group by c1.rwo_id, c1.rwo_code, c1.app_code";
        return new HashSet<>(templ.query(sql, new Object[] { node.getId(), node.getId() }, nodeMapper));
    }

    public Set<Node> filter(Set<Node> nodes, ExpandBehavior behavior) {
        if(behavior == ExpandBehavior.ALWAYS) {
            return new HashSet<>(nodes);
        }
        String behSql = behaviorFunction(behavior);
        behSql = behSql + "(node_id)";
        String array = Util.pgArray(nodes.stream().map( n -> Long.toString(n.getId())).collect(Collectors.toList()));
        List<Node> list = templ.query("select node_id from (select unnest(?) node_id) where " + behSql, new Object[]{array}, nodeMapper);
        return new HashSet<>(list);
    }

}
