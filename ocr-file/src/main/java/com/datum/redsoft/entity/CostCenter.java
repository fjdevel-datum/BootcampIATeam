package com.datum.redsoft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa un centro de costo
 * Permite asignar facturas a diferentes departamentos o proyectos
 */
@Entity
@Table(name = "cost_centers", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Data
public class CostCenter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Código de centro de costo es obligatorio")
    public String code;
    
    @Column(nullable = false)
    @NotBlank(message = "Nombre de centro de costo es obligatorio")
    public String name;
    
    public String description;
    
    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
