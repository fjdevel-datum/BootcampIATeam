package com.datum.redsoft.dto.request;

import com.datum.redsoft.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la actualización de una factura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceRequest {
    
    private Long cardId;
    
    private Long countryId;
    
    private String path;
    
    private String fileName;
    
    private InvoiceStatus status;
}
