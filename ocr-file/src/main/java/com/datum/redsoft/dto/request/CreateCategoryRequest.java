package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación de una categoría
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {
    
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;
    
    private String description;
    
    @Builder.Default
    private Boolean isActive = true;
}
