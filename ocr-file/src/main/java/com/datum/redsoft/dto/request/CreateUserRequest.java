package com.datum.redsoft.dto.request;

import com.datum.redsoft.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la creación de nuevos usuarios
 * Contiene todas las validaciones necesarias para crear un usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @Email(message = "Email debe tener formato válido")
    @NotBlank(message = "Email es obligatorio")
    private String email;
    
    @NotBlank(message = "Nombre es obligatorio")
    private String name;
    
    //@NotBlank(message = "Keycloak ID es obligatorio")
    private String keycloakId;
    
    @NotNull(message = "Rol es obligatorio")
    private UserRole role;
    
    @NotNull(message = "ID de empresa es obligatorio")
    private Long companyId;
    
    @NotNull(message = "ID de país es obligatorio")
    private Long countryId;
}