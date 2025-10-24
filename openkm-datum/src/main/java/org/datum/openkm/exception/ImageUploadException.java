package org.datum.openkm.exception;

public class ImageUploadException extends OpenKMException {

    public ImageUploadException(String message) {
        super(message);
    }

    public ImageUploadException(String message, int statusCode) {
        super(message, statusCode);
    }

    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageUploadException(String message, int statusCode, Throwable cause) {
        super(message, statusCode, cause);
    }
}
