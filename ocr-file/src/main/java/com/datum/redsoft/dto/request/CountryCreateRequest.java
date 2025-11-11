package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para crear un Country
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryCreateRequest {
    
    @NotBlank(message = "El código ISO es obligatorio")
    @Size(max = 10, message = "El código ISO no puede tener más de 10 caracteres")
    private String isoCode;
    
    @NotBlank(message = "El nombre del país es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String name;
}