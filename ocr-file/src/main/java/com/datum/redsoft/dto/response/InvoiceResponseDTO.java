package com.datum.redsoft.dto.response;

import com.datum.redsoft.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para una factura - Solo campos esenciales
 * Información básica necesaria para el cliente
 */
@Data
@AllArgsConstructor
public class InvoiceResponseDTO {
    
    private Long id;
    private String userName;
    private String cardMaskedNumber;
    private String companyName;
    private String countryName;
    private String path;
    private String fileName;
    private InvoiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
