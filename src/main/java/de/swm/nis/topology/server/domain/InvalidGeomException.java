package de.swm.nis.topology.server.domain;

public class InvalidGeomException extends Exception{

    public InvalidGeomException() {
    }

    public InvalidGeomException(String message) {
        super(message);
    }

    public InvalidGeomException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGeomException(Throwable cause) {
        super(cause);
    }

    public InvalidGeomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
