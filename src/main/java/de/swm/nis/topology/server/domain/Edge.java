package de.swm.nis.topology.server.domain;

public class Edge {

    private RWO rwo;
    private Node source;
    private Node target;
    private String geom;
    private double length;

    public RWO getRwo() {
        return rwo;
    }

    public void setRwo(RWO rwo) {
        this.rwo = rwo;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "rwo=" + rwo +
                ", source=" + source +
                ", target=" + target +
                ", geom='" + geom + '\'' +
                ", length=" + length +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (Double.compare(edge.length, length) != 0) return false;
        if (rwo != null ? !rwo.equals(edge.rwo) : edge.rwo != null) return false;
        if (source != null ? !source.equals(edge.source) : edge.source != null) return false;
        if (target != null ? !target.equals(edge.target) : edge.target != null) return false;
        return !(geom != null ? !geom.equals(edge.geom) : edge.geom != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = rwo != null ? rwo.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        temp = Double.doubleToLongBits(length);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

