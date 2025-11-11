package com.datum.redsoft.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la actualización de una categoría
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {
    
    private String name;
    
    private String description;
    
    private Boolean isActive;
}
