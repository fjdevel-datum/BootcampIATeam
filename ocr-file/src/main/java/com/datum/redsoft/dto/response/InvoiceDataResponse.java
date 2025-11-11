package com.datum.redsoft.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para datos de factura extraídos
 * Representa la información estructurada extraída de facturas/recibos
 */
public class InvoiceDataResponse {
    
    @JsonProperty("company_name")
    @NotBlank
    private String companyName;
    
    @JsonProperty("invoice_date")
    private String invoiceDate;
    
    @JsonProperty("total_amount")
    private String totalAmount;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("invoice_number")
    private String invoiceNumber;
    
    @JsonProperty("extraction_method")
    private String extractionMethod; // "AI" o "REGEX"
    
    @JsonProperty("confidence_score")
    private Double confidenceScore; // 0.0 a 1.0

    // Constructor vacío
    public InvoiceDataResponse() {}

    // Constructor completo
    public InvoiceDataResponse(String companyName, String invoiceDate, String totalAmount, 
                              String currency, String invoiceNumber, String extractionMethod, 
                              Double confidenceScore) {
        this.companyName = companyName;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.invoiceNumber = invoiceNumber;
        this.extractionMethod = extractionMethod;
        this.confidenceScore = confidenceScore;
    }

    // Getters y Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getExtractionMethod() {
        return extractionMethod;
    }

    public void setExtractionMethod(String extractionMethod) {
        this.extractionMethod = extractionMethod;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    @Override
    public String toString() {
        return "InvoiceDataResponse{" +
                "companyName='" + companyName + '\'' +
                ", invoiceDate='" + invoiceDate + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", currency='" + currency + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", extractionMethod='" + extractionMethod + '\'' +
                ", confidenceScore=" + confidenceScore +
                '}';
    }
}