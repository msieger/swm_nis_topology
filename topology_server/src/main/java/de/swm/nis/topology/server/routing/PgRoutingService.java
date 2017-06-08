package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.database.Schema;
import de.swm.nis.topology.server.database.Util;
import de.swm.nis.topology.server.domain.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PgRoutingService implements RoutingService{

    @Autowired
    private JdbcTemplate templ;

    @Autowired
    private PgRoutingResultMapper resultMapper;

    @Transactional
    @Override
    public List<RoutingResult> route(String network, Node from, List<Node> to, Node ignore) {
        Schema.set(templ, network, Schema.PUBLIC);
        List<String> toIds = to.stream().map(x -> Integer.toString((int)x.getId())).collect(Collectors.toList());
        String sql = String.format("select * from pgr_dijkstra('select * from %s.pgr_edge', ?::int4, ?::int4[], false)",
                network);
        List<PgRoutingResult> rows = templ.query(sql,
                new Object[] { from.getId(), Util.pgArray(toIds) }, resultMapper);
        Map<Long, RoutingResult> result = new HashMap<>();
        for(Node node : to) {
            result.put(node.getId(), new RoutingResult());
        }
        for(PgRoutingResult pgres : rows) {
            RoutingResult route = result.get(pgres.getTarget().getId());
            if(route.getNodes() == null) {
                route.setNodes(new ArrayList<>());
            }
            route.getNodes().add(pgres.getNode());
        }
        return new ArrayList<>(result.values());
    }
}
