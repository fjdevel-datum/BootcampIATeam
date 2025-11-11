package com.datum.redsoft.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la actualización de un centro de costo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCostCenterRequest {
    
    private String code;
    
    private String name;
    
    private String description;
    
    private Boolean isActive;
}
