package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para actualizar una Company
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateRequest {
    
    @Size(max = 200, message = "El nombre no puede tener más de 200 caracteres")
    private String name;
    
    private Long countryId;
    
    @Size(max = 500, message = "La dirección no puede tener más de 500 caracteres")
    private String address;
}