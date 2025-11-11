package com.datum.redsoft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para una categoría
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
