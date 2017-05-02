package de.swm.nis.topology.server.routing;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.QueryGraph;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.FastestWeighting;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class GraphhopperService implements RoutingService{

    @Autowired
    private Logger log;

    @Autowired
    private JdbcTemplate templ;

    @Autowired
    private NodeService nodeService;

    private String graphFolder = "graphhopper";

    private Map<String, GraphHopperStorage> graphs = new HashMap<>();

    private FlagEncoder flagEncoder = new CarFlagEncoder();

    private GraphHopperStorage getGraph(String network) {
        if(graphs.containsKey(network)) {
            log.debug("Graph for network " + network + " was already loaded");
            return graphs.get(network);
        }
        GraphBuilder gb = getBuilder(network);
        GraphHopperStorage graph;
        try{
            graph = gb.load();
            log.info("Successfully loaded graph for network " + network + " from storage");
        }catch(IllegalStateException e) {
            graph = buildGraph(gb, network);
        }
        graphs.put(network, graph);
        return graph;
    }

    private String forNetwork(String network) {
        return Paths.get(graphFolder, network).toAbsolutePath().toString();
    }

    private GraphBuilder getBuilder(String network) {
        EncodingManager em = new EncodingManager(flagEncoder);
        return new GraphBuilder(em).setLocation(forNetwork(network)).setStore(true);
    }

    private GraphHopperStorage buildGraph(GraphBuilder gb, String network) {
        log.info("Building GraphHopper data for network " + network + " from source.");
        GraphHopperStorage graph = gb.create();
        Set<SimpleEdge> edges = nodeService.getAllSimpleEdges(network);
        int i = 0;
        for(SimpleEdge edge : edges) {
            if(i % (edges.size() / 10) == 0) {
                log.debug(i + "/" + edges.size() + " edges processed");
            }
            EdgeIteratorState ghEdge = graph.edge(
                    (int)edge.getSource().getId(), (int)edge.getTarget().getId());
            ghEdge.setDistance(edge.getDistance());
            i++;
        }
        graph.flush();
        return graph;
    }

    @Override
    public RoutingResult route(String network, Node from, Node to) {
        GraphHopperStorage graph = getGraph(network);
        com.graphhopper.routing.Path path = new Dijkstra(graph, new FastestWeighting(flagEncoder), TraversalMode.NODE_BASED)
                .calcPath((int)from.getId(), (int)to.getId());
        return null;
    }
}
