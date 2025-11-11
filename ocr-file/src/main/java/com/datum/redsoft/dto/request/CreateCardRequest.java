package com.datum.redsoft.dto.request;

import com.datum.redsoft.enums.CardType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la creación de tarjetas
 * Contiene la información necesaria para crear una nueva tarjeta
 */
@Data
public class CreateCardRequest {
    
    @NotBlank(message = "Número de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Número de tarjeta debe tener entre 13-19 dígitos")
    private String cardNumber;
    
    @NotBlank(message = "Nombre del titular es obligatorio")
    private String holderName;
    
    @NotNull(message = "Tipo de tarjeta es obligatorio")
    private CardType cardType;
    
    @NotNull(message = "Fecha de expiración es obligatoria")
    @Future(message = "Fecha de expiración debe ser futura")
    private LocalDate expirationDate;
    
    @NotBlank(message = "Banco emisor es obligatorio")
    private String issuerBank;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Límite de crédito debe ser mayor a cero")
    private BigDecimal creditLimit;
    
    @Size(max = 255, message = "Descripción no puede exceder 255 caracteres")
    private String description;
    
    @NotNull(message = "ID de usuario es obligatorio")
    @Positive(message = "ID de usuario debe ser positivo")
    private Long userId;
    
    @NotNull(message = "ID de empresa es obligatorio")
    @Positive(message = "ID de empresa debe ser positivo")
    private Long companyId;
}