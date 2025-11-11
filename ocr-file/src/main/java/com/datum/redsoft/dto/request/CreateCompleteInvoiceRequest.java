package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear un Invoice completo con sus InvoiceFields
 * Maneja la creación transaccional de ambas entidades
 */
@Data
public class CreateCompleteInvoiceRequest {
    
    // Datos del Invoice
    @NotNull(message = "ID de usuario es obligatorio")
    private Long userId;
    
    @NotNull(message = "ID de empresa es obligatorio")
    private Long companyId;
    
    @NotNull(message = "ID de país es obligatorio")
    private Long countryId;
    
    private Long cardId; // Opcional
    
    @NotBlank(message = "Path es obligatorio")
    private String path;
    
    private String fileName;
    
    // Datos del InvoiceField (extraídos por OCR/LLM)
    @NotBlank(message = "Nombre del proveedor es obligatorio")
    private String vendorName;
    
    private LocalDate invoiceDate;
    
    @DecimalMin(value = "0.0", message = "Monto total debe ser mayor o igual a cero")
    private BigDecimal totalAmount;
    
    private String currency;
    
    private String concept;
    
    private Long categoryId; // Opcional
    
    private Long costCenterId; // Opcional
    
    private String clientVisited;
    
    private String notes;
}