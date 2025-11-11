package com.datum.redsoft.entity;

import com.datum.redsoft.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa una factura/invoice en el sistema
 * Contiene información básica de la factura
 * Los detalles OCR se almacenan en InvoiceField
 */
@Entity
@Table(name = "invoices")
@Data
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuario es obligatorio")
    public User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    public Card card;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull(message = "Empresa es obligatoria")
    public Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @NotNull(message = "País es obligatorio")
    public Country country;
    
    @Column(name = "path", nullable = false)
    @NotBlank(message = "Path es obligatorio")
    public String path;
    
    @Column(name = "file_name")
    public String fileName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Estado es obligatorio")
    public InvoiceStatus status = InvoiceStatus.DRAFT;
    
    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public InvoiceField invoiceField;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
