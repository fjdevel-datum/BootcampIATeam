package com.datum.redsoft.enums;

/**
 * Enumeración que define los tipos de facturas/invoices
 * Clasifica las facturas según su origen y propósito
 */
public enum InvoiceType {
    EXPENSE("Gasto"),
    RECEIPT("Recibo"),
    BILL("Factura"),
    TICKET("Ticket"),
    VOUCHER("Comprobante"),
    TAX_RECEIPT("Recibo Fiscal"),
    CREDIT_NOTE("Nota de Crédito"),
    DEBIT_NOTE("Nota de Débito");
    
    private final String displayName;
    
    InvoiceType(String displayName) {
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