package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.database.Schema;
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
    public List<RoutingResult> route(String network, Node from, List<Node> to) {
        Schema.set(templ, network, Schema.PUBLIC);
        /*List<PgRoutingResult> results = templ.query(
                "select * from pgr_astar('select * from pgr_edge', ?::int4, ?::int4, false, false)",
                new Object[] { from.getId(), to.getId() }, resultMapper);*/
        RoutingResult result = new RoutingResult(null);
        //result.setNodes(results.stream().map(r -> r.getNode()).collect(Collectors.toList()));
        //return result;
        return null;
    }
}
