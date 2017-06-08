package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;

import java.util.List;

public class RoutingResult {

    private List<Node> nodes;

    public RoutingResult(List<Node> nodes) {
        this.nodes = nodes;
    }

    public RoutingResult() {

    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public boolean found() {
        return nodes != null;
    }

    public Node getEnd() {
        return nodes.get(nodes.size() - 1);
    }
}
