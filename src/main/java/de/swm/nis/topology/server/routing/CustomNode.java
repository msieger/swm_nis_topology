package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;

public class CustomNode {

    private final Node node;

    private final Edge shortest;

    private final CustomNode previous;

    public CustomNode(Node node, Edge shortest, CustomNode previous) {
        this.node = node;
        this.shortest = shortest;
        this.previous = previous;
    }

    public Node getNode() {
        return node;
    }

    public Edge getShortest() {
        return shortest;
    }

    public CustomNode getPrevious() {
        return previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomNode that = (CustomNode) o;

        if (node != null ? !node.equals(that.node) : that.node != null) return false;
        if (shortest != null ? !shortest.equals(that.shortest) : that.shortest != null) return false;
        return !(previous != null ? !previous.equals(that.previous) : that.previous != null);

    }

    @Override
    public int hashCode() {
        int result = node != null ? node.hashCode() : 0;
        result = 31 * result + (shortest != null ? shortest.hashCode() : 0);
        result = 31 * result + (previous != null ? previous.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CustomNode{" +
                "node=" + node +
                ", shortest=" + shortest +
                ", previous=" + previous +
                '}';
    }


}
