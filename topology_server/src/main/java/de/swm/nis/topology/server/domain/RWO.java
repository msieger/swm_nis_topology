package de.swm.nis.topology.server.domain;

public class RWO {

    private long id;
    private String type;
    private String field;

    public RWO() {
    }

    public RWO(long id, String type, String field) {
        this.id = id;
        this.type = type;
        this.field = field;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
