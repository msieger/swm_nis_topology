package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.RoutingResult;
import de.swm.nis.topology.server.routing.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ShortestPath {

    @Autowired
    private RoutingService routingService;

    @Autowired
    private NodeService nodeService;

    @Transactional
    public Path find(Node from, Node to) {
        RoutingResult route = routingService.route(from, to);
        Path path = new Path();
        path.setNodes(route.getNodes());
        //path.setGeometry(nodeService.buildGeometry(path.getNodes()));
        return path;
    }

}
