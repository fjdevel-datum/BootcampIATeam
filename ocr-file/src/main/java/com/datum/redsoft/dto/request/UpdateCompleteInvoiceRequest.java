package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para actualizar un Invoice completo con sus InvoiceFields
 * Maneja la actualización transaccional de ambas entidades
 * Los campos path, fileName, cardId y status no se actualizan
 */
@Data
public class UpdateCompleteInvoiceRequest {
    
    // IDs de las entidades a actualizar
    private Long idInvoice;         // ID del Invoice
    private Long id;                // ID del InvoiceField
    
    // Datos del Invoice que se pueden actualizar
    private Long countryId;
    
    // Datos del InvoiceField que se pueden actualizar
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
