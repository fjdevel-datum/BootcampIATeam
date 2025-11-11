package com.datum.redsoft.enums;

/**
 * Enumeración que define los estados posibles de una factura/invoice
 * Controla el flujo de procesamiento y aprobación de facturas
 */
public enum InvoiceStatus {
    DRAFT("Borrador"),
    PENDING("Pendiente"),
    PROCESSING("Procesando"),
    PROCESSED("Procesada"),
    APPROVED("Aprobada"),
    REJECTED("Rechazada"),
    PAID("Pagada"),
    CANCELLED("Cancelada"),
    ERROR("Error");
    
    private final String displayName;
    
    InvoiceStatus(String displayName) {
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