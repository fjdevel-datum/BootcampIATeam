package com.datum.redsoft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para InvoiceField
 * Contiene toda la información de los campos de la factura
 */
@Data
@AllArgsConstructor
public class InvoiceFieldResponseDTO {
    
    private Long id;
    private Long invoiceId;
    private String vendorName;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private String currency;
    private String concept;
    
    // Información de categoría y centro de costo
    private String categoryName;
    private String costCenterName;
    
    private String clientVisited;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}