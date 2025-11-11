package com.datum.redsoft.enums;

/**
 * Enumeración que define los tipos de tarjetas de crédito/débito
 * Representa las diferentes categorías de tarjetas utilizadas para pagos
 */
public enum CardType {
    CREDIT("Crédito"),
    DEBIT("Débito"),
    CORPORATE("Corporativa"),
    PREPAID("Prepagada"),
    VIRTUAL("Virtual");
    
    private final String displayName;
    
    CardType(String displayName) {
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