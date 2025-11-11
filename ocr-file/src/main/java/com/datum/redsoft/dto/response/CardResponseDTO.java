package com.datum.redsoft.dto.response;

import com.datum.redsoft.enums.CardStatus;
import com.datum.redsoft.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para tarjetas - Solo información esencial
 * Muestra únicamente los datos más importantes de la tarjeta
 */
@Data
@AllArgsConstructor
public class CardResponseDTO {
    
    private Long id;
    private String maskedCardNumber;
    private String holderName;
    private CardType cardType;
    private LocalDate expirationDate;
    private String issuerBank;
    private BigDecimal creditLimit;
    private CardStatus status;
    private String description;
    
    // Información básica de relaciones
    private String userName;
    private Long companyId;
    private String companyName;
    
    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}