package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;

import java.util.List;

public class ReachableProviderResult {

    private List<Node> nodes;

    private List<RWO> providers;

    public List<RWO> getProviders() {
        return providers;
    }

    public void setProviders(List<RWO> providers) {
        this.providers = providers;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
