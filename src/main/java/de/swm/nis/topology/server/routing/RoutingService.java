package de.swm.nis.topology.server.routing;


import de.swm.nis.topology.server.domain.Node;

import java.util.Set;

public interface RoutingService {

    Path route(Node from, Node to);

}
