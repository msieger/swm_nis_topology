package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.domain.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PgRoutingService implements RoutingService{

    @Autowired
    private JdbcTemplate templ;

    @Autowired
    private PgRoutingResultMapper resultMapper;

    @Override
    public RoutingResult route(Node from, Node to) {
        String edgeSql = "select *, 1 cost, 1 reverse_cost from pgr_edges";
        List<PgRoutingResult> results = templ.query(
                String.format("select * from pgr_astar('%s', ?, ?, false)", edgeSql),
                new Object[] { from.getId(), to.getId() }, resultMapper);
        RoutingResult result = new RoutingResult();
        result.setNodes(results.stream().map(r -> r.getNode()).collect(Collectors.toList()));
        return result;
    }
}
