package de.swm.nis.topology.server.database;

public class NoEdgeException extends Exception{

    public NoEdgeException() {
    }

    public NoEdgeException(String message) {
        super(message);
    }

    public NoEdgeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoEdgeException(Throwable cause) {
        super(cause);
    }

    public NoEdgeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
