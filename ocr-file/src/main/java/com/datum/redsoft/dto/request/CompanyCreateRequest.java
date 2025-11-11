package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para crear una Company
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreateRequest {
    
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 200, message = "El nombre no puede tener más de 200 caracteres")
    private String name;
    
    @NotNull(message = "El ID del país es obligatorio")
    private Long countryId;
    
    @Size(max = 500, message = "La dirección no puede tener más de 500 caracteres")
    private String address;
}