package com.datum.redsoft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para un centro de costo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostCenterResponseDTO {
    
    private Long id;
    
    private String code;
    
    private String name;
    
    private String description;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
