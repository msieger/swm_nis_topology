package de.swm.nis.topology.server.domain;

public class Connection {

    private long nodeId;
    private long rwoId;
    private int rwoCode;
    private int appCode;
    private int pointIdx;

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public long getRwoId() {
        return rwoId;
    }

    public void setRwoId(long rwoId) {
        this.rwoId = rwoId;
    }

    public int getRwoCode() {
        return rwoCode;
    }

    public void setRwoCode(int rwoCode) {
        this.rwoCode = rwoCode;
    }

    public int getAppCode() {
        return appCode;
    }

    public void setAppCode(int appCode) {
        this.appCode = appCode;
    }

    public int getPointIdx() {
        return pointIdx;
    }

    public void setPointIdx(int pointIdx) {
        this.pointIdx = pointIdx;
    }
}
