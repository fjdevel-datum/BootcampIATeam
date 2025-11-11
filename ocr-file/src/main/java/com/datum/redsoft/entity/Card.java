package com.datum.redsoft.entity;

import com.datum.redsoft.enums.CardStatus;
import com.datum.redsoft.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una tarjeta de crédito/débito en el sistema
 * Maneja la información de tarjetas asociadas a usuarios y empresas
 */
@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)  
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Número de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Número de tarjeta debe tener entre 13-19 dígitos")
    public String cardNumber;
    
    @Column(nullable = false)
    @NotBlank(message = "Número enmascarado es obligatorio")
    public String maskedCardNumber;
    
    @Column(nullable = false)
    @NotBlank(message = "Nombre del titular es obligatorio")
    public String holderName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Tipo de tarjeta es obligatorio")
    public CardType cardType;
    
    @Column(nullable = false)
    @NotNull(message = "Fecha de expiración es obligatoria")
    @Future(message = "Fecha de expiración debe ser futura")
    public LocalDate expirationDate;
    
    @Column(nullable = false)
    @NotBlank(message = "Banco emisor es obligatorio")
    public String issuerBank;
    
    @Column(precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Límite de crédito debe ser mayor a cero")
    public BigDecimal creditLimit;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Estado de la tarjeta es obligatorio")
    public CardStatus status = CardStatus.ACTIVE;
    
    @Column(length = 255)
    @Size(max = 255, message = "Descripción no puede exceder 255 caracteres")
    public String description;
    
    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuario es obligatorio")
    public User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull(message = "Empresa es obligatoria")
    public Company company;
    
    // Campos de auditoría
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    

    
    /**
     * Verifica si la tarjeta está activa
     */
    public boolean isActive() {
        return CardStatus.ACTIVE.equals(this.status);
    }
    
    /**
     * Verifica si la tarjeta está expirada
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expirationDate) || 
               CardStatus.EXPIRED.equals(this.status);
    }
    
    /**
     * Calcula el saldo disponible (para tarjetas de crédito)
     */
    public BigDecimal getAvailableBalance() {
        if (creditLimit == null) {
            return BigDecimal.ZERO;
        }
        
        return creditLimit;
    }
}