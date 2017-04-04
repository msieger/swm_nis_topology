package de.swm.nis.topology.server.domain;

public class Edge {

    private RWO rwo;
    private Node from;
    private Node to;

    public RWO getRwo() {
        return rwo;
    }

    public void setRwo(RWO rwo) {
        this.rwo = rwo;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }
}

