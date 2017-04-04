package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;

import java.util.Collections;
import java.util.Set;

public class BlockedPath {

    private Set<Node> nodes = Collections.emptySet();

    private Set<Node> blockingNodes = Collections.emptySet();;

    private String geometry = "";

    public BlockedPath() {
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public Set<Node> getBlockingNodes() {
        return blockingNodes;
    }

    public void setBlockingNodes(Set<Node> blockingNodes) {
        this.blockingNodes = blockingNodes;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }
}
