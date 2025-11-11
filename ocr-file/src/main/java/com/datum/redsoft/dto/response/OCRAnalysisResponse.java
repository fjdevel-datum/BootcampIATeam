package com.datum.redsoft.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para respuestas de análisis OCR
 * Encapsula tanto el texto extraído como los datos estructurados de la factura
 */
public class OCRAnalysisResponse {
    
    @NotNull
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("ocr_text")
    private String ocrText;
    
    @Valid
    @JsonProperty("invoice_data")
    private OCRResponseDTO invoiceData;
    
    @JsonProperty("processing_time_ms")
    private Long processingTimeMs;
    
    @JsonProperty("error_message")
    private String errorMessage;

    // Constructor vacío
    public OCRAnalysisResponse() {}

    // Constructor para éxito
    public OCRAnalysisResponse(String ocrText, OCRResponseDTO invoiceData, Long processingTimeMs) {
        this.status = "success";
        this.ocrText = ocrText;
        this.invoiceData = invoiceData;
        this.processingTimeMs = processingTimeMs;
    }

    // Constructor para error
    public OCRAnalysisResponse(String errorMessage) {
        this.status = "error";
        this.errorMessage = errorMessage;
    }

    // Getters y Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOcrText() {
        return ocrText;
    }

    public void setOcrText(String ocrText) {
        this.ocrText = ocrText;
    }

    public OCRResponseDTO getInvoiceData() {
        return invoiceData;
    }

    public void setInvoiceData(OCRResponseDTO invoiceData) {
        this.invoiceData = invoiceData;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "OCRAnalysisResponse{" +
                "status='" + status + '\'' +
                ", ocrText='" + (ocrText != null ? ocrText.substring(0, Math.min(50, ocrText.length())) + "..." : null) + '\'' +
                ", invoiceData=" + invoiceData +
                ", processingTimeMs=" + processingTimeMs +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}