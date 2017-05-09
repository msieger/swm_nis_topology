package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;

import java.util.List;

public class RoutingResult {

    private List<Node> nodes;

    public RoutingResult(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
