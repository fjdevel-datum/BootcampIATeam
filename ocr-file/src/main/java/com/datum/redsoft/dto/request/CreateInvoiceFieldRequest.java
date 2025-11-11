package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear un InvoiceField
 * Contiene los datos extraídos del OCR y procesados por LLM
 */
@Data
public class CreateInvoiceFieldRequest {
    
    @NotNull(message = "ID de factura es obligatorio")
    private Long invoiceId;
    
    @NotBlank(message = "Nombre del proveedor es obligatorio")
    private String vendorName;
    
    private LocalDate invoiceDate;
    
    @DecimalMin(value = "0.0", message = "Monto total debe ser mayor o igual a cero")
    private BigDecimal totalAmount;
    
    private String currency;
    
    private String concept;
    
    private Long categoryId;
    
    private Long costCenterId;
    
    private String clientVisited;
    
    private String notes;
}