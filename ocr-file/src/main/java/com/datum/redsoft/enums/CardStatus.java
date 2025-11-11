package com.datum.redsoft.enums;

/**
 * Enumeración que define los estados posibles de una tarjeta
 * Controla el ciclo de vida y la disponibilidad de las tarjetas
 */
public enum CardStatus {
    ACTIVE("Activa"),
    INACTIVE("Inactiva"),
    EXPIRED("Expirada"),
    BLOCKED("Bloqueada"),
    SUSPENDED("Suspendida"),
    CANCELLED("Cancelada");
    
    private final String displayName;
    
    CardStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}