package com.datum.redsoft.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para respuestas de estado del servicio
 */
public class StatusResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("ocr_service_available")
    private boolean ocrServiceAvailable;
    
    @JsonProperty("extraction_service_available")
    private boolean extractionServiceAvailable;
    
    @JsonProperty("extraction_method")
    private String extractionMethod;
    
    @JsonProperty("timestamp")
    private Long timestamp;

    // Constructor vacío
    public StatusResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor completo
    public StatusResponse(String status, boolean ocrServiceAvailable, 
                         boolean extractionServiceAvailable, String extractionMethod) {
        this();
        this.status = status;
        this.ocrServiceAvailable = ocrServiceAvailable;
        this.extractionServiceAvailable = extractionServiceAvailable;
        this.extractionMethod = extractionMethod;
    }

    // Getters y Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOcrServiceAvailable() {
        return ocrServiceAvailable;
    }

    public void setOcrServiceAvailable(boolean ocrServiceAvailable) {
        this.ocrServiceAvailable = ocrServiceAvailable;
    }

    public boolean isExtractionServiceAvailable() {
        return extractionServiceAvailable;
    }

    public void setExtractionServiceAvailable(boolean extractionServiceAvailable) {
        this.extractionServiceAvailable = extractionServiceAvailable;
    }

    public String getExtractionMethod() {
        return extractionMethod;
    }

    public void setExtractionMethod(String extractionMethod) {
        this.extractionMethod = extractionMethod;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "status='" + status + '\'' +
                ", ocrServiceAvailable=" + ocrServiceAvailable +
                ", extractionServiceAvailable=" + extractionServiceAvailable +
                ", extractionMethod='" + extractionMethod + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}