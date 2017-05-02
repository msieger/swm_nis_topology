package de.swm.nis.topology.server.domain;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import de.swm.nis.topology.server.database.LineStringParser;
import de.swm.nis.topology.server.service.NotLineStringException;

public class Edge {

    private static final LineStringParser parser = new LineStringParser();

    private RWO rwo;
    private Node source;
    private Node target;
    private LineString geom;

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

    public LineString getGeom() {
        return geom;
    }

    public void setGeomWkt(String geomWkt) throws InvalidGeomException {
        try {
            geom = parser.parse(geomWkt);
        } catch (NotLineStringException | ParseException e) {
            throw new InvalidGeomException("The geometry '" + geomWkt + "' is invalid", e);
        }
    }

    public void setGeomWkb(byte[] data) throws InvalidGeomException {
        try {
            geom = parser.parse(data);
        } catch (NotLineStringException | ParseException e) {
            throw new InvalidGeomException("The geometry is invalid", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (rwo != null ? !rwo.equals(edge.rwo) : edge.rwo != null) return false;
        if (source != null ? !source.equals(edge.source) : edge.source != null) return false;
        if (target != null ? !target.equals(edge.target) : edge.target != null) return false;
        return !(geom != null ? !geom.equals(edge.geom) : edge.geom != null);

    }

    @Override
    public int hashCode() {
        int result = rwo != null ? rwo.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "rwo=" + rwo +
                ", source=" + source +
                ", target=" + target +
                ", geom=" + geom +
                '}';
    }
}

