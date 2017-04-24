package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;

import java.util.List;

public class Path {

    private List<Long> nodes;

    private String geom;

    public Path(List<Long> nodes, String geom) {
        this.nodes = nodes;
        this.geom = geom;
    }

    public List<Long> getNodes() {
        return nodes;
    }

    public String getGeom() {
        return geom;
    }
}
