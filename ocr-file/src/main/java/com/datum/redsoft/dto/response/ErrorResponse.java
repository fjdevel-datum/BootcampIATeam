package com.datum.redsoft.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para respuestas de error estandarizadas
 * Siguiendo el principio de Single Responsibility
 */
public class ErrorResponse {
    
    @JsonProperty("status")
    private String status = "error";
    
    @JsonProperty("error_code")
    private String errorCode;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("details")
    private String details;
    
    @JsonProperty("timestamp")
    private Long timestamp;
    
    // Constructor vacío
    public ErrorResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Constructor completo
    public ErrorResponse(String errorCode, String message, String details) {
        this();
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }
    
    // Constructor simple
    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, null);
    }

    // Getters y Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status='" + status + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}