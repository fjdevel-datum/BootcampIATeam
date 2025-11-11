package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.request.CreateUserRequest;
import com.datum.redsoft.dto.request.UpdateUserRequest;
import com.datum.redsoft.dto.response.UserResponseDTO;
import com.datum.redsoft.enums.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * Interface para el servicio de gestión de usuarios
 * Define los métodos principales para operaciones CRUD y lógica de negocio de User
 * Facilita testing, mocking y futuras implementaciones alternativas
 */
public interface IUserService {
    
    /**
     * Obtiene todos los usuarios con sus relaciones cargadas
     * @return Lista de UserResponseDTO
     */
    List<UserResponseDTO> getAllUsers();
    
    /**
     * Obtiene un usuario por ID con sus relaciones
     * @param id ID del usuario
     * @return Optional con UserResponseDTO si existe
     */
    Optional<UserResponseDTO> getUserById(Long id);
    
    /**
     * Obtiene un usuario por email
     * @param email Email del usuario
     * @return Optional con UserResponseDTO si existe
     */
    Optional<UserResponseDTO> getUserByEmail(String email);
    
    /**
     * Obtiene un usuario por Keycloak ID
     * @param keycloakId Keycloak ID del usuario
     * @return Optional con UserResponseDTO si existe
     */
    Optional<UserResponseDTO> getUserByKeycloakId(String keycloakId);
    
    /**
     * Obtiene usuarios por empresa
     * @param companyId ID de la empresa
     * @return Lista de UserResponseDTO de la empresa
     */
    List<UserResponseDTO> getUsersByCompany(Long companyId);
    
    /**
     * Obtiene solo usuarios activos
     * @return Lista de UserResponseDTO activos
     */
    List<UserResponseDTO> getActiveUsers();
    
    /**
     * Busca usuarios por nombre (búsqueda parcial)
     * @param name Nombre o parte del nombre
     * @return Lista de UserResponseDTO que coinciden
     */
    List<UserResponseDTO> searchUsersByName(String name);
    
    /**
     * Crea un nuevo usuario
     * @param request Datos del nuevo usuario
     * @return UserResponseDTO del usuario creado
     * @throws IllegalArgumentException si hay errores de validación
     */
    UserResponseDTO createUser(CreateUserRequest request);
    
    /**
     * Actualiza un usuario existente
     * @param id ID del usuario a actualizar
     * @param request Datos actualizados
     * @return Optional con UserResponseDTO actualizado si existe
     * @throws IllegalArgumentException si hay errores de validación
     */
    Optional<UserResponseDTO> updateUser(Long id, UpdateUserRequest request);
    
    /**
     * Cambia el estado de un usuario
     * @param id ID del usuario
     * @param status Nuevo estado
     * @return true si se actualizó correctamente, false si no existe
     */
    boolean changeUserStatus(Long id, UserStatus status);
    
    /**
     * Desactiva un usuario (cambio de estado a INACTIVE)
     * @param id ID del usuario
     * @return true si se desactivó correctamente, false si no existe
     */
    boolean deactivateUser(Long id);
    
    /**
     * Activa un usuario (cambio de estado a ACTIVE)
     * @param id ID del usuario
     * @return true si se activó correctamente, false si no existe
     */
    boolean activateUser(Long id);
    
    /**
     * Suspende un usuario (cambio de estado a SUSPENDED)
     * @param id ID del usuario
     * @return true si se suspendió correctamente, false si no existe
     */
    boolean suspendUser(Long id);
    
    /**
     * Sincroniza un usuario con Keycloak
     * @param keycloakId ID de Keycloak
     * @return Optional con UserResponseDTO si existe
     */
    Optional<UserResponseDTO> syncWithKeycloak(String keycloakId);
    
}