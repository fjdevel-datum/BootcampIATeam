package com.datum.redsoft.repository;

import com.datum.redsoft.entity.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Category
 * Maneja el acceso a datos de categorías
 */
@ApplicationScoped
public class CategoryRepository {
    
    @PersistenceContext
    EntityManager em;
    
    public void persist(Category category) {
        em.persist(category);
    }
    
    public Category merge(Category category) {
        return em.merge(category);
    }
    
    public void remove(Category category) {
        em.remove(em.contains(category) ? category : em.merge(category));
    }
    
    public Optional<Category> findByIdOptional(Long id) {
        Category category = em.find(Category.class, id);
        return Optional.ofNullable(category);
    }
    
    public List<Category> listAll() {
        return em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
                .getResultList();
    }
    
    public Optional<Category> findByName(String name) {
        List<Category> categories = em.createQuery(
                "SELECT c FROM Category c WHERE c.name = :name", Category.class)
                .setParameter("name", name)
                .getResultList();
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }
    
    public List<Category> findActiveCategories() {
        return em.createQuery(
                "SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.name", Category.class)
                .getResultList();
    }
    
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }
}
