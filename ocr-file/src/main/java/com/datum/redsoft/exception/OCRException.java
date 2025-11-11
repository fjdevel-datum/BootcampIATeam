package com.datum.redsoft.exception;

/**
 * Excepción base para errores en servicios de OCR
 */
public class OCRException extends Exception {
    
    public OCRException(String message) {
        super(message);
    }
    
    public OCRException(String message, Throwable cause) {
        super(message, cause);
    }
}