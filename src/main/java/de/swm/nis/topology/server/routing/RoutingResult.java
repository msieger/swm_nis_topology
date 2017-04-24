package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;

import java.util.List;

public class RoutingResult {

    private List<Edge> edges;

    public RoutingResult(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
