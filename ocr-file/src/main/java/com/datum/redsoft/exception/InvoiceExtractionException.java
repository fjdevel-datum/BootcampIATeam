package com.datum.redsoft.exception;

/**
 * Excepción para errores en la extracción de datos de facturas
 */
public class InvoiceExtractionException extends Exception {
    
    public InvoiceExtractionException(String message) {
        super(message);
    }
    
    public InvoiceExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}