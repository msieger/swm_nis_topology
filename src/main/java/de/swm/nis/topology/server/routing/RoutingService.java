package de.swm.nis.topology.server.routing;


import de.swm.nis.topology.server.domain.Node;

public interface RoutingService {

    RoutingResult route(Node from, Node to);

}
