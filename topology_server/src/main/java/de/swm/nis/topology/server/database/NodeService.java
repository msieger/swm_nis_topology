package de.swm.nis.topology.server.database;

import de.swm.nis.topology.server.domain.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sun.java2d.pipe.SpanShapeRenderer;
import sun.plugin.dom.exception.InvalidStateException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NodeService {

    private static final int FETCH_SIZE = 10 * 1024;

    public enum ExpandBehavior {ALWAYS, NEVER, IF_OPEN}

    @Autowired
    private EdgeMapper edgeMapper;

    @Autowired
    private SimpleEdgeMapper simpleEdgeMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private StringMapper stringMapper;

    @Autowired
    private ConnectionMapper connectionMapper;

    @Autowired
    private IntMapper intMapper;

    @Autowired
    private JdbcTemplate templ;

    @Autowired
    private Logger log;

    @Transactional
    public Set<Node> getNodes(String network, RWO rwo) {
        Schema.set(templ, network);
        return new HashSet<>(templ.query("select node_id from connection where rwo_id = ? and rwo_code = ? and app_code = ?",
                new Object[]{rwo.getId(), rwo.getCode(), rwo.getApp()}, nodeMapper));
    }

    private boolean tableExists(String schema, String table) {
        return templ.query("select 1 from information_schema.tables where table_schema = ? and table_name = ?",
                new Object[] {schema, table}, intMapper).size() == 1;
    }

    private boolean columnExists(String schema, String table, String column) {
        return templ.query("select 1 from information_schema.columns " +
                "where table_schema = ? and table_name = ? and column_name = ?",
                new Object[] {schema, table, column}, intMapper).size() == 1;
    }

    @Transactional
    public Node findNode(String network, String rwoName, String geomName, double x, double y, int srid) {
        if(!tableExists(network, rwoName)) {
            throw new RuntimeException("Table " + network + "." + geomName + " does not exist");
        }
        if(!columnExists(network, rwoName, geomName)) {
            throw new RuntimeException("Column " + network + "." + rwoName + "." + geomName + " does not exist");
        }
        Schema.set(templ, network, Schema.PUBLIC);
        String closestPointSql = String.format(
                " select path[1]" +
                " from ST_DumpPoints(%s.%s) " +
                " order by ST_Distance(ST_DumpPoints.geom, (select * from point)) limit 1", rwoName, geomName, rwoName);
        String sql = String.format(
                " with point as (select ST_SetSRID(ST_MakePoint(?,?),?))" +
                " select rwo_id[3], (%s)" +
                " from %s" +
                " where ST_DWithin(%s, (select * from point), 100)" +
                " order by ST_Distance(%s, (select * from point))" +
                " limit 1",
                closestPointSql, rwoName, geomName, geomName);
        AbstractMap.SimpleEntry<Long, Integer> closestIndex = templ.queryForObject(
                sql,
                new Object[] {x, y, srid},
                (rs, nowNum) -> new AbstractMap.SimpleEntry<Long, Integer>(rs.getLong(1), rs.getInt(2))
                );
        List<Connection> cons = templ.query(
                " select *" +
                " from connection con" +
                " join definition def on con.rwo_code = def.rwo_code" +
                " join geom_attribute attr on con.rwo_code = attr.rwo_code" +
                " and con.app_code = attr.app_code" +
                " where con.rwo_id = ?",
                new Object[] {closestIndex.getKey()},
                connectionMapper);
        Optional<Connection> closestNode = cons.stream().min((l, r) -> {
            int d1 = Math.abs(l.getPointIdx() - closestIndex.getValue());
            int d2 = Math.abs(r.getPointIdx() - closestIndex.getValue());
            return d1 - d2;
        });
        if(!closestNode.isPresent()) {
            throw new RuntimeException("No node was found");
        }
        return new Node(closestNode.get().getNodeId());
    }

    private static String behaviorFunction(ExpandBehavior b) {
        switch (b) {
            case ALWAYS:
                return "";
            case NEVER:
                return "is_closable";
            case IF_OPEN:
                return "is_closed";
            default:
                throw new RuntimeException("Case not implemented");
        }
    }

    @Transactional
    public Set<Edge> getNeighbors(String network, Node node, ExpandBehavior behavior) {
        Schema.set(templ, network, Schema.PUBLIC);
        String behSql = behaviorFunction(behavior);
        if(!behSql.isEmpty()) {
            behSql = " and not " + behSql + "(source)";
        }
        String sql = "select source, target, ST_AsBinary(geom) geom from neighbor where source = ?"
                + behSql;
        return new HashSet<>(templ.query(sql, new Object[] { node.getId()}, edgeMapper))
                .stream().filter(edge -> {
                    if(edge.getGeom() == null) {
                        log.warn(edge + " has null geometry and is ignored");
                        return false;
                    }
                    return true;
                }).collect(Collectors.toSet());
    }

    public interface SimpleEdgeCallback {

        void accept(SimpleEdge edge);

    }

    @Transactional
    public Edge getShortestEdge(String network, Node from, Node to) throws NoEdgeException {
        Schema.set(templ, network, Schema.PUBLIC);
        List<Edge> edges = templ.query(
                " select source, target, ST_AsBinary(geom) geom" +
                " from neighbor" +
                " where source=? and target=?" +
                " order by ST_Length(geom)" +
                " limit 1", new Object[]{from.getId(), to.getId()}, edgeMapper);
        if(edges.size() == 1) {
            return edges.get(0);
        }
        throw new NoEdgeException("Request for shortest edge between " + from + " and " + to + ", which does not exist");
    }

    @Transactional
    public void getAllSimpleEdges(String network, SimpleEdgeCallback cb) {
        Schema.set(templ, network, Schema.PUBLIC);
        templ.setFetchSize(FETCH_SIZE);
        templ.query(" select *" +
                    " from (select source, target, ST_Length(geom) length " +
                    " from all_neighbor" +
                    " ) s" +
                    " where length is not null",
                new RowCallbackHandler() {

                    private SimpleEdge edge = new SimpleEdge();

                    @Override
                    public void processRow(ResultSet rs) throws SQLException, DataAccessException {
                        while(rs.next()) {
                            edge.setSource(new Node(rs.getLong("source")));
                            edge.setTarget(new Node(rs.getLong("target")));
                            edge.setDistance(rs.getDouble("length"));
                            cb.accept(edge);
                        }
                    }
                });
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

    public String collect(Collection<String> geometries) {
        return String.format("MULTILINESTRING(%s)", String.join(",", geometries));
    }

    @Transactional
    public Set<Node> providers(String network) {
        Schema.set(templ, network);
        return new HashSet<>(templ.query("select * from providing_node", nodeMapper));
    }
}
