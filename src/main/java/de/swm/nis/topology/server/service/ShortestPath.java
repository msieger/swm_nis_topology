package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.CustomService;
import de.swm.nis.topology.server.routing.RoutingResult;
import de.swm.nis.topology.server.routing.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShortestPath {

    @Autowired
    private CustomService routingService;

    @Autowired
    private NodeService nodeService;

    @Transactional
    public Path find(String network, Node from, Node to) {
        RoutingResult routing = routingService.route(network, from, to);
        List<String> geoms = routing.getEdges().stream().map(Edge::getGeom).collect(Collectors.toList());
        String geom = nodeService.collect(geoms);
        List<Long> nodes = new ArrayList<>();
        if(routing.getEdges().size() > 0) {
            nodes.add(routing.getEdges().get(0).getSource().getId());
            nodes.addAll(routing.getEdges().stream().map(x -> x.getTarget().getId()).collect(Collectors.toList()));
        }
        return new Path(nodes, geom);
    }

}
