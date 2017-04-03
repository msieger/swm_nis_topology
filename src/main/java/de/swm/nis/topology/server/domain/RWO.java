package de.swm.nis.topology.server.domain;

public class RWO {

    private long id;
    private int code;
    private int app;

    public RWO(long rwoId, int rwoCode, int appCode) {
        this.id = rwoId;
        this.code = rwoCode;
        this.app = appCode;
    }

    public RWO() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public long getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public int getApp() {
        return app;
    }
}
