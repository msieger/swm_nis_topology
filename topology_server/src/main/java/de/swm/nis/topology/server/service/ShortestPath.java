package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.database.NoEdgeException;
import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.CustomService;
import de.swm.nis.topology.server.routing.GraphhopperService;
import de.swm.nis.topology.server.routing.RoutingResult;
import de.swm.nis.topology.server.routing.RoutingService;
import gnu.trove.procedure.TIntProcedure;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ShortestPath {

    @Autowired
    private RoutingService routingService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private Logger log;

    @Transactional
    public Path find(String network, Node from, Node to) {
        RoutingResult routing = routingService.route(network, from, Arrays.asList(to)).get(0);
        if(!routing.found()) {
            return null;
        }
        List<Edge> edges = new ArrayList<>();
        routing.getNodes().stream().forEach(new Consumer<Node>() {

            private Node prev = null;

            @Override
            public void accept(Node node) {
                if(prev != null) {
                    try {
                        edges.add(nodeService.getShortestEdge(network, prev, node));
                    } catch (NoEdgeException e) {
                        log.warn("Edge between " + prev + " and " + node + " was requested, but did not exist");
                    }
                }
                prev = node;
            }
        });
        List<String> geoms = edges.stream().map(e -> e.getGeom().toText()).collect(Collectors.toList());
        String geom = nodeService.collect(geoms);
        List<Long> nodes = new ArrayList<>();
        if(edges.size() > 0) {
            nodes.add(edges.get(0).getSource().getId());
            nodes.addAll(edges.stream().map(x -> x.getTarget().getId()).collect(Collectors.toList()));
        }
        return new Path(nodes, geom);
    }

}
