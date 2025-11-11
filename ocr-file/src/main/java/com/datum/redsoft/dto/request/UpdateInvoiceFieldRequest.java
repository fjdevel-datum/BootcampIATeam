package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para actualizar un InvoiceField
 * Permite actualización parcial de campos
 */
@Data
public class UpdateInvoiceFieldRequest {
    
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