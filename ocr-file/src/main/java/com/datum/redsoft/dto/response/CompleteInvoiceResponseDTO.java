package com.datum.redsoft.dto.response;

import com.datum.redsoft.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Invoice completo con InvoiceField
 * Contiene toda la información de la factura y sus campos extraídos
 */
@Data
@AllArgsConstructor
public class CompleteInvoiceResponseDTO {
    
    // Datos del Invoice
    private Long invoiceId;
    private String userName;
    private String cardMaskedNumber;
    private String companyName;
    private String countryName;
    private String path;
    private String fileName;
    private InvoiceStatus status;
    private LocalDateTime invoiceCreatedAt;
    private LocalDateTime invoiceUpdatedAt;
    
    // Datos del InvoiceField
    private Long invoiceFieldId;
    private String vendorName;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private String currency;
    private String concept;
    private String categoryName;
    private String costCenterName;
    private String clientVisited;
    private String notes;
    private LocalDateTime fieldCreatedAt;
    private LocalDateTime fieldUpdatedAt;
}