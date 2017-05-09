package de.swm.nis.topology.server.routing;

import com.google.common.collect.HashBiMap;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphHopperStorage;
import de.swm.nis.topology.server.database.NoEdgeException;
import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.SimpleEdge;
import gnu.trove.procedure.TIntProcedure;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphHopperNetwork {

    private static final int NODE_MAP_INITIAL_SIZE = 100 * 1000;

    private final NodeService nodeService;
    private final String network;
    private final Logger log;
    private String graphFolder = "graphhopper";

    private final GraphHopperStorage graph;
    private final FlagEncoder encoder = new FootFlagEncoder();
    private HashBiMap<Integer, Integer> nodeIds = HashBiMap.create(NODE_MAP_INITIAL_SIZE);

    public GraphHopperNetwork(NodeService nodeService, String network, Logger log) {
        this.nodeService = nodeService;
        this.network = network;
        this.log = log;
        this.graph = getGraph();
    }

    private GraphHopperStorage getGraph() {
        GraphBuilder gb = getBuilder();
        GraphHopperStorage graph;
        try{
            graph = gb.load();
            File nodeIdFile = nodeIdFile();
            try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(nodeIdFile))) {
                nodeIds = (HashBiMap<Integer, Integer>) in.readObject();
            } catch (IOException e) {
                throw new RuntimeException("Unable to load " + nodeIdFile, e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to load " + nodeIdFile, e);
            }
            log.info("Successfully loaded graph for network " + network + " from storage");
        }catch(IllegalStateException e) {
            graph = buildGraph(gb);
        }
        return graph;
    }

    private String folder() {
        return Paths.get(graphFolder, network).toAbsolutePath().toString();
    }

    private File nodeIdFile() {
        return new File(folder(), "node_map").getAbsoluteFile();
    }

    private GraphBuilder getBuilder() {
        EncodingManager em = new EncodingManager(encoder);
        return new GraphBuilder(em).setLocation(folder()).setStore(true);
    }

    private GraphHopperStorage buildGraph(GraphBuilder gb) {
        log.info("Building GraphHopper data for network " + network + " from source.");
        GraphHopperStorage graph = gb.create();
        nodeService.getAllSimpleEdges(network, new NodeService.SimpleEdgeCallback() {

            private int count = 0;
            private int nextFreeNodeId = 1;

            private int getNode(int oldNode) {
                Integer newNode = nodeIds.get(oldNode);
                if(newNode == null) {
                    newNode = nextFreeNodeId++;
                    nodeIds.put(oldNode, newNode);
                }
                return newNode;
            }

            @Override
            public void accept(SimpleEdge edge) {
                if(count != 0 && count % 100000 == 0) {
                    log.info("Processed " + count + " edges");
                }
                int fromNode = getNode((int)edge.getSource().getId());
                int toNode = getNode((int)edge.getTarget().getId());
                graph.edge(fromNode, toNode, edge.getDistance(), true);
                count++;
            }
        });
        graph.flush();
        File nodeMapFile = nodeIdFile();
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nodeMapFile))) {
            out.writeObject(nodeIds);
        } catch (IOException e) {
            throw new RuntimeException("Can not write to " + nodeMapFile, e);
        }
        return graph;
    }

    public RoutingResult route(int from, int to) {
        if(!nodeIds.containsKey(from) || !nodeIds.containsKey(to)) {
            return null;
        }
        com.graphhopper.routing.Path path = new Dijkstra(graph, new ShortestWeighting(encoder), TraversalMode.NODE_BASED)
                .calcPath(nodeIds.get(from), nodeIds.get(to));
        List<Node> nodes = new ArrayList<>();
        path.calcNodes().forEach(nodeId -> {
            nodes.add(new Node(nodeIds.inverse().get(nodeId)));
            return true;
        });
        return new RoutingResult(nodes);
    }
}
