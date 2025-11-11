package com.datum.redsoft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de respuesta para datos extraídos por OCR y procesados por LLM
 * Contiene únicamente los 4 campos esenciales extraídos de la factura
 */
@Data
@AllArgsConstructor
public class OCRResponseDTO {
    
    private String vendorName;    // Nombre del proveedor/empresa que emitió la factura
    private String invoiceDate;   // Fecha de emisión de la factura
    private String totalAmount;   // Monto total de la factura
    private String currency;      // Moneda utilizada
}