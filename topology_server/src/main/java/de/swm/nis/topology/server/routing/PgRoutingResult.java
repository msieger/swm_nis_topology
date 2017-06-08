package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.domain.Node;


public class PgRoutingResult {

    private Node node;
    private Node target;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }
}
