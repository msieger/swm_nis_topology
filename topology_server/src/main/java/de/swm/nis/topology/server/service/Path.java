package de.swm.nis.topology.server.service;

import java.util.List;

public class Path {

    private List<Long> nodes;

    private String geometry;

    public Path(List<Long> nodes, String geom) {
        this.nodes = nodes;
        this.geometry = geom;
    }

    public List<Long> getNodes() {
        return nodes;
    }

    public String getGeometry() {
        return geometry;
    }
}
