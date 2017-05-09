package de.swm.nis.topology.server.routing;


import de.swm.nis.topology.server.domain.Node;

import java.util.List;

public interface RoutingService {

    List<RoutingResult> route(String network, Node from, List<Node> to);

}
