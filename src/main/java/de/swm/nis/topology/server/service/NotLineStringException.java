package de.swm.nis.topology.server.service;

public class NotLineStringException extends Exception{

    public NotLineStringException() {
    }

    public NotLineStringException(String message) {
        super(message);
    }

    public NotLineStringException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLineStringException(Throwable cause) {
        super(cause);
    }

    public NotLineStringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
