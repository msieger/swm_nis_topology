package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.RWO;

import java.util.List;

public class UnreachableConsumerResult {

    private List<BlockedPath> blockedPaths;

    private List<RWO> consumers;

    public List<BlockedPath> getBlockedPaths() {
        return blockedPaths;
    }

    public void setBlockedPaths(List<BlockedPath> blockedPaths) {
        this.blockedPaths = blockedPaths;
    }

    public List<RWO> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<RWO> consumers) {
        this.consumers = consumers;
    }
}
