package org.datum.openkm.exception;

public class OpenKMException extends RuntimeException {

    private final int statusCode;

    public OpenKMException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public OpenKMException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public OpenKMException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }

    public OpenKMException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
