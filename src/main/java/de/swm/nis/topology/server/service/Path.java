package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;

import java.util.List;

public class Path {

    private List<Node> nodes;

    private String geometry;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }
}
