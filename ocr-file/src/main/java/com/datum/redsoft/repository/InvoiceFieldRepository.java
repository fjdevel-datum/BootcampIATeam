package com.datum.redsoft.repository;

import com.datum.redsoft.entity.InvoiceField;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la entidad InvoiceField
 */
@ApplicationScoped
public class InvoiceFieldRepository implements PanacheRepository<InvoiceField> {
    
    /**
     * Encuentra un campo de factura por ID de factura
     */
    public Optional<InvoiceField> findByInvoiceId(Long invoiceId) {
        return find("invoice.id", invoiceId).firstResultOptional();
    }
    
    /**
     * Encuentra campos de factura por nombre de proveedor
     */
    public List<InvoiceField> findByVendorName(String vendorName) {
        return find("LOWER(vendorName) LIKE LOWER(?1)", "%" + vendorName + "%").list();
    }
    
    /**
     * Encuentra campos de factura por categoría
     */
    public List<InvoiceField> findByCategoryId(Long categoryId) {
        return find("category.id", categoryId).list();
    }
    
    /**
     * Encuentra campos de factura por centro de costo
     */
    public List<InvoiceField> findByCostCenterId(Long costCenterId) {
        return find("costCenter.id", costCenterId).list();
    }
    
    /**
     * Encuentra campos de factura por moneda
     */
    public List<InvoiceField> findByCurrency(String currency) {
        return find("currency", currency).list();
    }
    
    /**
     * Busca campos de factura que no tienen categoría asignada
     */
    public List<InvoiceField> findWithoutCategory() {
        return find("category IS NULL").list();
    }
    
    /**
     * Busca campos de factura que no tienen centro de costo asignado
     */
    public List<InvoiceField> findWithoutCostCenter() {
        return find("costCenter IS NULL").list();
    }
}