package de.swm.nis.topology.server.domain;

public class SimpleEdge {

    private Node source;
    private Node target;
    private double distance;

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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleEdge that = (SimpleEdge) o;

        if (Double.compare(that.distance, distance) != 0) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return !(target != null ? !target.equals(that.target) : that.target != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = source != null ? source.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        temp = Double.doubleToLongBits(distance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SimpleEdge{" +
                "source=" + source +
                ", target=" + target +
                ", distance=" + distance +
                '}';
    }
}
