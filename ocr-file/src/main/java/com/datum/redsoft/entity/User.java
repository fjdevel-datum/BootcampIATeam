package com.datum.redsoft.entity;

import com.datum.redsoft.enums.UserRole;
import com.datum.redsoft.enums.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa a los usuarios del sistema
 * Incluye colaboradores y administradores con autenticación vía Keycloak
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "keycloak_id")
       })
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(unique = true, nullable = false)
    @Email(message = "Email debe tener formato válido")
    @NotBlank(message = "Email es obligatorio")
    public String email;
    
    @Column(nullable = false)
    @NotBlank(message = "Nombre es obligatorio")
    public String name;
    
    @Column(name = "keycloak_id", unique = true, nullable = false)
    //@NotBlank(message = "Keycloak ID es obligatorio")
    public String keycloakId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Rol es obligatorio")
    public UserRole role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull(message = "Empresa es obligatoria")
    public Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @NotNull(message = "País es obligatorio")
    public Country country;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserStatus status = UserStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    
    /**
     * Constructor por defecto requerido por JPA
     */
    public User() {}
    
    /**
     * Constructor con parámetros principales
     */
    public User(String email, String name, String keycloakId, UserRole role, Company company, Country country) {
        this.email = email;
        this.name = name;
        this.keycloakId = keycloakId;
        this.role = role;
        this.company = company;
        this.country = country;
        this.status = UserStatus.ACTIVE;
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
     * Verifica si el usuario es colaborador
     */
    public boolean isCollaborator() {
        return this.role == UserRole.COLLABORATOR;
    }
}