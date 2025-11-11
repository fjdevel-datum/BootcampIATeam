package com.datum.redsoft.dto.response;

import com.datum.redsoft.enums.UserRole;
import com.datum.redsoft.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para usuarios
 * Incluye toda la información del usuario y sus relaciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    
    private Long id;
    private String email;
    private String name;
    private String keycloakId;
    private UserRole role;
    private CompanyDTO company;
    private CountryDTO country;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Constructor simplificado sin timestamps para testing
     */
    public UserResponseDTO(Long id, String email, String name, String keycloakId, 
                          UserRole role, CompanyDTO company, CountryDTO country, UserStatus status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.keycloakId = keycloakId;
        this.role = role;
        this.company = company;
        this.country = country;
        this.status = status;
    }
    
    /**
     * Verifica si el usuario está activo
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
    
    /**
     * Verifica si el usuario es administrador
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
    
    /**
     * Obtiene el nombre para mostrar del rol
     */
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : null;
    }
    
    /**
     * Obtiene el nombre para mostrar del estado
     */
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : null;
    }
}