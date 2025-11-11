package com.datum.redsoft.repository;

import com.datum.redsoft.entity.Invoice;
import com.datum.redsoft.enums.InvoiceStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Invoice
 * Maneja el acceso a datos de facturas
 */
@ApplicationScoped
public class InvoiceRepository {
    
    @PersistenceContext
    EntityManager em;
    
    public void persist(Invoice invoice) {
        em.persist(invoice);
    }
    
    public Invoice merge(Invoice invoice) {
        return em.merge(invoice);
    }
    
    public void remove(Invoice invoice) {
        em.remove(em.contains(invoice) ? invoice : em.merge(invoice));
    }
    
    public Optional<Invoice> findByIdOptional(Long id) {
        Invoice invoice = em.find(Invoice.class, id);
        return Optional.ofNullable(invoice);
    }
    
    public List<Invoice> listAll() {
        return em.createQuery("SELECT i FROM Invoice i ORDER BY i.createdAt DESC", Invoice.class)
                .getResultList();
    }
    
    public List<Invoice> findByUserId(Long userId) {
        return em.createQuery(
                "SELECT i FROM Invoice i WHERE i.user.id = :userId ORDER BY i.createdAt DESC", Invoice.class)
                .setParameter("userId", userId)
                .getResultList();
    }
    
    public List<Invoice> findByCompanyId(Long companyId) {
        return em.createQuery(
                "SELECT i FROM Invoice i WHERE i.company.id = :companyId ORDER BY i.createdAt DESC", Invoice.class)
                .setParameter("companyId", companyId)
                .getResultList();
    }
    
    public List<Invoice> findByCardId(Long cardId) {
        return em.createQuery(
                "SELECT i FROM Invoice i WHERE i.card.id = :cardId ORDER BY i.createdAt DESC", Invoice.class)
                .setParameter("cardId", cardId)
                .getResultList();
    }
    
    public List<Invoice> findByCountryId(Long countryId) {
        return em.createQuery(
                "SELECT i FROM Invoice i WHERE i.country.id = :countryId ORDER BY i.createdAt DESC", Invoice.class)
                .setParameter("countryId", countryId)
                .getResultList();
    }
    
    public List<Invoice> findByStatus(InvoiceStatus status) {
        return em.createQuery(
                "SELECT i FROM Invoice i WHERE i.status = :status ORDER BY i.createdAt DESC", Invoice.class)
                .setParameter("status", status)
                .getResultList();
    }
    
    public List<Invoice> findDraftInvoices() {
        return findByStatus(InvoiceStatus.DRAFT);
    }
    
    public List<Invoice> findPendingInvoices() {
        return findByStatus(InvoiceStatus.PENDING);
    }
    
    public List<Invoice> findProcessedInvoices() {
        return findByStatus(InvoiceStatus.PROCESSED);
    }
    
    public List<Invoice> findApprovedInvoices() {
        return findByStatus(InvoiceStatus.APPROVED);
    }
    
    public Long countByUserId(Long userId) {
        return em.createQuery(
                "SELECT COUNT(i) FROM Invoice i WHERE i.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
    
    public Long countByStatus(InvoiceStatus status) {
        return em.createQuery(
                "SELECT COUNT(i) FROM Invoice i WHERE i.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }
}
