package com.datum.redsoft.repository;

import com.datum.redsoft.entity.User;
import com.datum.redsoft.enums.UserRole;
import com.datum.redsoft.enums.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad User
 * Proporciona métodos de consulta personalizados usando Panache
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    /**
     * Busca un usuario por email
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    
    /**
     * Busca un usuario por Keycloak ID
     * @param keycloakId ID de Keycloak
     * @return Optional con el usuario si existe
     */
    public Optional<User> findByKeycloakId(String keycloakId) {
        return find("keycloakId", keycloakId).firstResultOptional();
    }
    
    /**
     * Busca usuarios por empresa
     * @param companyId ID de la empresa
     * @return Lista de usuarios de la empresa
     */
    public List<User> findByCompanyId(Long companyId) {
        return find("company.id", companyId).list();
    }
    
    /**
     * Busca usuarios por rol
     * @param role Rol del usuario
     * @return Lista de usuarios con el rol especificado
     */
    public List<User> findByRole(UserRole role) {
        return find("role", role).list();
    }
    
    /**
     * Busca solo usuarios activos
     * @return Lista de usuarios activos
     */
    public List<User> findActiveUsers() {
        return find("status", UserStatus.ACTIVE).list();
    }
    
    /**
     * Busca usuarios por empresa y estado
     * @param companyId ID de la empresa
     * @param status Estado del usuario
     * @return Lista de usuarios que coinciden con los criterios
     */
    public List<User> findByCompanyIdAndStatus(Long companyId, UserStatus status) {
        return find("company.id = ?1 and status = ?2", companyId, status).list();
    }
    
    /**
     * Busca un usuario por ID incluyendo sus relaciones
     * @param id ID del usuario
     * @return Optional con el usuario y sus relaciones cargadas
     */
    public Optional<User> findByIdWithRelations(Long id) {
        return find("select u from User u " +
                   "left join fetch u.company c " +
                   "left join fetch c.country " +
                   "left join fetch u.country " +
                   "where u.id = ?1", id)
                .firstResultOptional();
    }
    
    /**
     * Obtiene todos los usuarios con sus relaciones cargadas
     * @return Lista de usuarios con relaciones
     */
    public List<User> findAllWithRelations() {
        return find("select u from User u " +
                   "left join fetch u.company c " +
                   "left join fetch c.country " +
                   "left join fetch u.country")
                .list();
    }
    
    /**
     * Busca usuarios por nombre (búsqueda parcial)
     * @param name Nombre o parte del nombre
     * @return Lista de usuarios que coinciden
     */
    public List<User> findByNameContaining(String name) {
        return find("lower(name) like lower(?1)", "%" + name + "%").list();
    }
    
    /**
     * Cuenta usuarios por empresa
     * @param companyId ID de la empresa
     * @return Número de usuarios en la empresa
     */
    public long countByCompanyId(Long companyId) {
        return count("company.id", companyId);
    }
    
    /**
     * Cuenta usuarios activos por empresa
     * @param companyId ID de la empresa
     * @return Número de usuarios activos en la empresa
     */
    public long countActiveUsersByCompanyId(Long companyId) {
        return count("company.id = ?1 and status = ?2", companyId, UserStatus.ACTIVE);
    }
    
    /**
     * Verifica si existe un usuario con el email dado
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
    
    /**
     * Verifica si existe un usuario con el Keycloak ID dado
     * @param keycloakId Keycloak ID a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existsByKeycloakId(String keycloakId) {
        return count("keycloakId", keycloakId) > 0;
    }
}