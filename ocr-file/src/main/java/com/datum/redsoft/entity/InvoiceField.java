package com.datum.redsoft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que almacena los campos extraídos de una factura mediante OCR
 * Contiene toda la información detallada de la factura
 */
@Entity
@Table(name = "invoice_fields")
@Data
public class InvoiceField {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @NotNull(message = "Factura es obligatoria")
    public Invoice invoice;
    
    @Column(name = "vendor_name", nullable = false)
    @NotBlank(message = "Nombre del proveedor es obligatorio")
    public String vendorName;
    
    @Column(name = "invoice_date", nullable = false)
    @NotNull(message = "Fecha de factura es obligatoria")
    public LocalDate invoiceDate;
    
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.01", message = "El monto total debe ser mayor a 0")
    @NotNull(message = "Monto total es obligatorio")
    public BigDecimal totalAmount;
    
    @Column(length = 3, nullable = false)
    @Pattern(regexp = "[A-Z]{3}", message = "La moneda debe seguir el formato ISO 4217 (ej: USD, EUR)")
    @NotBlank(message = "Moneda es obligatoria")
    public String currency;
    
    @Column(nullable = false)
    @NotBlank(message = "Concepto es obligatorio")
    public String concept;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    public Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_center_id")
    public CostCenter costCenter;
    
    @Column(name = "client_visited")
    public String clientVisited;
    
    public String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
