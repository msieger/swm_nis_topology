package de.swm.nis.topology.server.routing;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.QueryGraph;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.util.EdgeIteratorState;
import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.SimpleEdge;
import de.swm.nis.topology.server.service.Path;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GraphhopperService implements RoutingService{

    @Autowired
    private Logger log;

    @Autowired
    private JdbcTemplate templ;

    @Autowired
    private NodeService nodeService;

    private Map<String, GraphHopperNetwork> graphs = new HashMap<>();

    private GraphHopperNetwork getGraph(String network) {
        GraphHopperNetwork ghn = graphs.get(network);
        if(ghn == null) {
            ghn = new GraphHopperNetwork(nodeService, network, log);
            graphs.put(network, ghn);
        }
        return ghn;
    }

    @Override
    public List<RoutingResult> route(String network, Node from, List<Node> to) {
        GraphHopperNetwork graph = getGraph(network);
        return graph.route((int)from.getId(), to.stream().map(x -> (int)x.getId()).collect(Collectors.toList()));
    }
}
