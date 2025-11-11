package com.datum.redsoft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa una categoría de gasto
 * Permite clasificar las facturas según su tipo de gasto
 */
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Nombre de categoría es obligatorio")
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
