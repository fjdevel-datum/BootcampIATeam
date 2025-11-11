package com.datum.redsoft.dto.request;

import com.datum.redsoft.enums.CardStatus;
import com.datum.redsoft.enums.CardType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la actualización de tarjetas
 * Permite actualizar campos específicos de una tarjeta existente
 */
@Data
public class UpdateCardRequest {
    
    private String holderName;
    
    private CardType cardType;
    
    @Future(message = "Fecha de expiración debe ser futura")
    private LocalDate expirationDate;
    
    private String issuerBank;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Límite de crédito debe ser mayor a cero")
    private BigDecimal creditLimit;
    
    private CardStatus status;
    
    @Size(max = 255, message = "Descripción no puede exceder 255 caracteres")
    private String description;
}