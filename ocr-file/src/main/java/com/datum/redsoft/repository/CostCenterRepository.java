package com.datum.redsoft.repository;

import com.datum.redsoft.entity.CostCenter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CostCenter
 * Maneja el acceso a datos de centros de costo
 */
@ApplicationScoped
public class CostCenterRepository {
    
    @PersistenceContext
    EntityManager em;
    
    public void persist(CostCenter costCenter) {
        em.persist(costCenter);
    }
    
    public CostCenter merge(CostCenter costCenter) {
        return em.merge(costCenter);
    }
    
    public void remove(CostCenter costCenter) {
        em.remove(em.contains(costCenter) ? costCenter : em.merge(costCenter));
    }
    
    public Optional<CostCenter> findByIdOptional(Long id) {
        CostCenter costCenter = em.find(CostCenter.class, id);
        return Optional.ofNullable(costCenter);
    }
    
    public List<CostCenter> listAll() {
        return em.createQuery("SELECT c FROM CostCenter c ORDER BY c.code", CostCenter.class)
                .getResultList();
    }
    
    public Optional<CostCenter> findByCode(String code) {
        List<CostCenter> costCenters = em.createQuery(
                "SELECT c FROM CostCenter c WHERE c.code = :code", CostCenter.class)
                .setParameter("code", code)
                .getResultList();
        return costCenters.isEmpty() ? Optional.empty() : Optional.of(costCenters.get(0));
    }
    
    public List<CostCenter> findActiveCostCenters() {
        return em.createQuery(
                "SELECT c FROM CostCenter c WHERE c.isActive = true ORDER BY c.code", CostCenter.class)
                .getResultList();
    }
    
    public boolean existsByCode(String code) {
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM CostCenter c WHERE c.code = :code", Long.class)
                .setParameter("code", code)
                .getSingleResult();
        return count > 0;
    }
    
    public List<CostCenter> searchByName(String name) {
        return em.createQuery(
                "SELECT c FROM CostCenter c WHERE LOWER(c.name) LIKE LOWER(:name) ORDER BY c.code", CostCenter.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
}
