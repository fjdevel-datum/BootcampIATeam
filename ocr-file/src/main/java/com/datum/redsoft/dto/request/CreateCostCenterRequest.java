package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación de un centro de costo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCostCenterRequest {
    
    @NotBlank(message = "El código del centro de costo es obligatorio")
    private String code;
    
    @NotBlank(message = "El nombre del centro de costo es obligatorio")
    private String name;
    
    private String description;
    
    @Builder.Default
    private Boolean isActive = true;
}
