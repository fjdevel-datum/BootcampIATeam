package com.datum.redsoft.dto.request;

import com.datum.redsoft.enums.UserRole;
import com.datum.redsoft.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la actualización de usuarios existentes
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Email(message = "Email debe tener formato válido")
    private String email;
    
    private String name;
    
    private UserRole role;
    
    private Long companyId;
    
    private Long countryId;
    
    private UserStatus status;
}