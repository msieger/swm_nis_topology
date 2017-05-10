package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;

import java.util.List;

public class ReachableProviderResult {

    private List<Node> nodes;

    private List<RWO> rwos;

    public List<RWO> getRwos() {
        return rwos;
    }

    public void setRwos(List<RWO> rwos) {
        this.rwos = rwos;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
