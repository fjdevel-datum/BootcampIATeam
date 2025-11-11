package com.datum.redsoft.repository;

import com.datum.redsoft.entity.Card;
import com.datum.redsoft.enums.CardStatus;
import com.datum.redsoft.enums.CardType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Card
 * Proporciona acceso a datos y consultas personalizadas para tarjetas
 */
@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {
    
    /**
     * Busca una tarjeta por su número enmascarado
     * @param maskedCardNumber Número de tarjeta enmascarado
     * @return Optional con la tarjeta encontrada
     */
    public Optional<Card> findByMaskedCardNumber(String maskedCardNumber) {
        return find("maskedCardNumber", maskedCardNumber).firstResultOptional();
    }
    
    /**
     * Busca una tarjeta por su número real (para validaciones internas)
     * @param cardNumber Número de tarjeta real
     * @return Optional con la tarjeta encontrada
     */
    public Optional<Card> findByCardNumber(String cardNumber) {
        return find("cardNumber", cardNumber).firstResultOptional();
    }
    
    /**
     * Obtiene todas las tarjetas de un usuario
     * @param userId ID del usuario
     * @return Lista de tarjetas del usuario
     */
    public List<Card> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }
    
    /**
     * Obtiene todas las tarjetas de una empresa
     * @param companyId ID de la empresa
     * @return Lista de tarjetas de la empresa
     */
    public List<Card> findByCompanyId(Long companyId) {
        return find("company.id", companyId).list();
    }
    
    /**
     * Obtiene todas las tarjetas activas
     * @return Lista de tarjetas activas
     */
    public List<Card> findActiveCards() {
        return find("status", CardStatus.ACTIVE).list();
    }
    
    /**
     * Obtiene tarjetas por tipo
     * @param cardType Tipo de tarjeta
     * @return Lista de tarjetas del tipo especificado
     */
    public List<Card> findByCardType(CardType cardType) {
        return find("cardType", cardType).list();
    }
    
    /**
     * Obtiene tarjetas por estado
     * @param status Estado de la tarjeta
     * @return Lista de tarjetas con el estado especificado
     */
    public List<Card> findByStatus(CardStatus status) {
        return find("status", status).list();
    }
    
    /**
     * Obtiene tarjetas que expiran antes de una fecha específica
     * @param expirationDate Fecha límite
     * @return Lista de tarjetas que expiran antes de la fecha
     */
    public List<Card> findCardsExpiringBefore(LocalDate expirationDate) {
        return find("expirationDate < ?1", expirationDate).list();
    }
    
    /**
     * Obtiene tarjetas de un usuario con estado específico
     * @param userId ID del usuario
     * @param status Estado de la tarjeta
     * @return Lista de tarjetas del usuario con el estado especificado
     */
    public List<Card> findByUserIdAndStatus(Long userId, CardStatus status) {
        return find("user.id = ?1 and status = ?2", userId, status).list();
    }
    
    /**
     * Obtiene tarjetas activas de un usuario
     * @param userId ID del usuario
     * @return Lista de tarjetas activas del usuario
     */
    public List<Card> findActiveCardsByUser(Long userId) {
        return findByUserIdAndStatus(userId, CardStatus.ACTIVE);
    }
    
    /**
     * Busca tarjetas por nombre del titular (búsqueda case-insensitive)
     * @param holderName Nombre del titular
     * @return Lista de tarjetas que coinciden con el nombre
     */
    public List<Card> findByHolderName(String holderName) {
        return find("LOWER(holderName) LIKE LOWER(?1)", "%" + holderName + "%").list();
    }
    
    /**
     * Busca tarjetas por banco emisor (búsqueda case-insensitive)
     * @param bankName Nombre del banco
     * @return Lista de tarjetas del banco especificado
     */
    public List<Card> findByIssuerBank(String bankName) {
        return find("LOWER(issuerBank) LIKE LOWER(?1)", "%" + bankName + "%").list();
    }
    
    /**
     * Obtiene tarjetas de una empresa con estado específico
     * @param companyId ID de la empresa
     * @param status Estado de la tarjeta
     * @return Lista de tarjetas de la empresa con el estado especificado
     */
    public List<Card> findByCompanyIdAndStatus(Long companyId, CardStatus status) {
        return find("company.id = ?1 and status = ?2", companyId, status).list();
    }
    
    /**
     * Obtiene tarjetas activas de una empresa
     * @param companyId ID de la empresa
     * @return Lista de tarjetas activas de la empresa
     */
    public List<Card> findActiveCardsByCompany(Long companyId) {
        return findByCompanyIdAndStatus(companyId, CardStatus.ACTIVE);
    }
    
    /**
     * Cuenta tarjetas de un usuario por estado
     * @param userId ID del usuario
     * @param status Estado de la tarjeta
     * @return Número de tarjetas del usuario con el estado especificado
     */
    public long countByUserIdAndStatus(Long userId, CardStatus status) {
        return count("user.id = ?1 and status = ?2", userId, status);
    }
    
    /**
     * Verifica si existe una tarjeta con el número especificado
     * @param cardNumber Número de tarjeta
     * @return true si existe, false en caso contrario
     */
    public boolean existsByCardNumber(String cardNumber) {
        return count("cardNumber", cardNumber) > 0;
    }
    
    /**
     * Obtiene todas las tarjetas de un usuario ordenadas por fecha de creación
     * @param userId ID del usuario
     * @return Lista de tarjetas ordenadas por fecha de creación descendente
     */
    public List<Card> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return find("user.id = ?1 ORDER BY createdAt DESC", userId).list();
    }
    
    /**
     * Encuentra una tarjeta por ID incluyendo sus relaciones
     * @param id ID de la tarjeta
     * @return Optional con la tarjeta y sus relaciones cargadas
     */
    public Optional<Card> findByIdWithRelations(Long id) {
        return find("SELECT c FROM Card c LEFT JOIN FETCH c.user u LEFT JOIN FETCH c.company co WHERE c.id = ?1", id)
                .firstResultOptional();
    }
    
    /**
     * Obtiene todas las tarjetas con sus relaciones cargadas
     * @return Lista de tarjetas con relaciones
     */
    public List<Card> findAllWithRelations() {
        return find("SELECT c FROM Card c LEFT JOIN FETCH c.user u LEFT JOIN FETCH c.company co").list();
    }
    
    /**
     * Obtiene las facturas asociadas a una tarjeta con datos de Invoice e InvoiceField
     * @param cardId ID de la tarjeta
     * @return Lista de objetos con datos de factura y campos de factura
     */
    public List<Object[]> findInvoicesWithFieldsByCardId(Long cardId) {
        return getEntityManager().createQuery(
            "SELECT " +
                "if_.id, " +                    // ID del InvoiceField
                "i.id, " +                      // ID del Invoice
                "if_.vendorName, " +            // Nombre del proveedor
                "if_.concept, " +               // Concepto
                "cat.name, " +                  // Nombre de la categoría
                "if_.invoiceDate, " +           // Fecha de la factura
                "if_.totalAmount, " +           // Monto total
                "if_.currency, " +              // Moneda
                "if_.category.id, " +           // ID de la categoría
                "if_.costCenter.id, " +         // ID del centro de costos
                "cc.name, " +                   // Nombre del centro de costos
                "if_.clientVisited, " +         // Cliente visitado
                "if_.notes, " +                 // Notas
                "i.status, " +                  // Status del Invoice
                "i.country.id, " +              // Country ID del Invoice
                "i.path, " +                    // Path del archivo
                "i.fileName " +                 // Nombre del archivo
            "FROM Invoice i " +
            "JOIN InvoiceField if_ ON i.id = if_.invoice.id " +
            "LEFT JOIN Category cat ON if_.category.id = cat.id " +
            "LEFT JOIN CostCenter cc ON if_.costCenter.id = cc.id " +
            "WHERE i.card.id = :cardId " +
            "ORDER BY if_.invoiceDate DESC", 
            Object[].class)
            .setParameter("cardId", cardId)
            .getResultList();
    }
}