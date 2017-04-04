package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.domain.Node;

import java.util.List;

public class RoutingResult {

    private List<Node> nodes;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
