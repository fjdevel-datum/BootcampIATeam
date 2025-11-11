package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación de una factura
 * Estructura simplificada - los detalles OCR se manejan en InvoiceField
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;
    
    private Long cardId; // Opcional
    
    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long companyId;
    
    @NotNull(message = "El ID del país es obligatorio")
    private Long countryId;
    
    @NotBlank(message = "El path es obligatorio")
    private String path;
    
    private String fileName;
}
