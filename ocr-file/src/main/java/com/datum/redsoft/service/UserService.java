package com.datum.redsoft.service;

import com.datum.redsoft.dto.request.CreateUserRequest;
import com.datum.redsoft.dto.request.UpdateUserRequest;
import com.datum.redsoft.dto.response.CompanyDTO;
import com.datum.redsoft.dto.response.CountryDTO;
import com.datum.redsoft.dto.response.UserResponseDTO;
import com.datum.redsoft.entity.Company;
import com.datum.redsoft.entity.Country;
import com.datum.redsoft.entity.User;
import com.datum.redsoft.enums.UserStatus;
import com.datum.redsoft.repository.CompanyRepository;
import com.datum.redsoft.repository.CountryRepository;
import com.datum.redsoft.repository.UserRepository;
import com.datum.redsoft.service.interfaces.IUserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las operaciones CRUD y lógica de negocio de User
 * Maneja la creación, actualización, consulta y gestión de estado de usuarios
 */
@ApplicationScoped
public class UserService implements IUserService {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    
    @Inject
    UserRepository userRepository;
    
    @Inject
    CompanyRepository companyRepository;
    
    @Inject
    CountryRepository countryRepository;
    
    /**
     * Obtiene todos los usuarios con sus relaciones cargadas
     * @return Lista de UserResponseDTO
     */
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");
        return userRepository.findAllWithRelations()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un usuario por ID con sus relaciones
     * @param id ID del usuario
     * @return Optional con UserResponseDTO si existe
     */
    public Optional<UserResponseDTO> getUserById(Long id) {
        logger.info("Buscando usuario con ID: " + id);
        return userRepository.findByIdWithRelations(id)
                .map(this::toDTO);
    }
    
    /**
     * Obtiene un usuario por email
     * @param email Email del usuario
     * @return Optional con UserResponseDTO si existe
     */
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        logger.info("Buscando usuario con email: " + email);
        return userRepository.findByEmail(email)
                .map(this::toDTO);
    }
    
    /**
     * Obtiene un usuario por Keycloak ID
     * @param keycloakId Keycloak ID del usuario
     * @return Optional con UserResponseDTO si existe
     */
    public Optional<UserResponseDTO> getUserByKeycloakId(String keycloakId) {
        logger.info("Buscando usuario con Keycloak ID: " + keycloakId);
        return userRepository.findByKeycloakId(keycloakId)
                .map(this::toDTO);
    }
    
    /**
     * Obtiene usuarios por empresa
     * @param companyId ID de la empresa
     * @return Lista de UserResponseDTO de la empresa
     */
    public List<UserResponseDTO> getUsersByCompany(Long companyId) {
        logger.info("Buscando usuarios de la empresa con ID: " + companyId);
        return userRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene solo usuarios activos
     * @return Lista de UserResponseDTO activos
     */
    public List<UserResponseDTO> getActiveUsers() {
        logger.info("Obteniendo usuarios activos");
        return userRepository.findActiveUsers()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca usuarios por nombre (búsqueda parcial)
     * @param name Nombre o parte del nombre
     * @return Lista de UserResponseDTO que coinciden
     */
    public List<UserResponseDTO> searchUsersByName(String name) {
        logger.info("Buscando usuarios con nombre que contiene: " + name);
        return userRepository.findByNameContaining(name)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea un nuevo usuario
     * @param request Datos del nuevo usuario
     * @return UserResponseDTO del usuario creado
     * @throws IllegalArgumentException si hay errores de validación
     */
    @Transactional
    public UserResponseDTO createUser(CreateUserRequest request) {
        logger.info("Creando nuevo usuario con email: " + request.getEmail());
        
        // Validar que email no esté en uso
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con este email: " + request.getEmail());
        }
        
        // Validar que keycloakId no esté en uso
        if (userRepository.existsByKeycloakId(request.getKeycloakId())) {
            throw new IllegalArgumentException("Ya existe un usuario con este Keycloak ID: " + request.getKeycloakId());
        }
        
        // Validar que empresa existe
        Company company = companyRepository.findByIdOptional(request.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa con ID: " + request.getCompanyId()));
        
        // Validar que país existe
        Country country = countryRepository.findByIdOptional(request.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el país con ID: " + request.getCountryId()));
        
        // Crear nuevo usuario
        User user = new User(
            request.getEmail(),
            request.getName(),
            request.getKeycloakId(),
            request.getRole(),
            company,
            country
        );
        
        userRepository.persist(user);
        logger.info("Usuario creado exitosamente con ID: " + user.id);
        
        return toDTO(user);
    }
    
    /**
     * Actualiza un usuario existente
     * @param id ID del usuario a actualizar
     * @param request Datos actualizados
     * @return Optional con UserResponseDTO actualizado si existe
     * @throws IllegalArgumentException si hay errores de validación
     */
    @Transactional
    public Optional<UserResponseDTO> updateUser(Long id, UpdateUserRequest request) {
        logger.info("Actualizando usuario con ID: " + id);
        
        Optional<User> userOpt = userRepository.findByIdWithRelations(id);
        
        if (userOpt.isEmpty()) {
            logger.warning("Usuario no encontrado con ID: " + id);
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Actualizar email si es diferente
        if (request.getEmail() != null && !request.getEmail().equals(user.email)) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Ya existe un usuario con este email: " + request.getEmail());
            }
            user.email = request.getEmail();
        }
        
        // Actualizar nombre
        if (request.getName() != null) {
            user.name = request.getName();
        }
        
        // Actualizar rol
        if (request.getRole() != null) {
            user.role = request.getRole();
        }
        
        // Actualizar empresa
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findByIdOptional(request.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa con ID: " + request.getCompanyId()));
            user.company = company;
        }
        
        // Actualizar país
        if (request.getCountryId() != null) {
            Country country = countryRepository.findByIdOptional(request.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el país con ID: " + request.getCountryId()));
            user.country = country;
        }
        
        // Actualizar estado
        if (request.getStatus() != null) {
            user.status = request.getStatus();
        }
        
        userRepository.persist(user);
        logger.info("Usuario actualizado exitosamente con ID: " + id);
        
        return Optional.of(toDTO(user));
    }
    
    /**
     * Cambia el estado de un usuario
     * @param id ID del usuario
     * @param status Nuevo estado
     * @return true si se actualizó correctamente, false si no existe
     */
    @Transactional
    public boolean changeUserStatus(Long id, UserStatus status) {
        logger.info("Cambiando estado del usuario " + id + " a: " + status);
        
        Optional<User> userOpt = userRepository.findByIdOptional(id);
        
        if (userOpt.isEmpty()) {
            logger.warning("Usuario no encontrado con ID: " + id);
            return false;
        }
        
        User user = userOpt.get();
        user.status = status;
        userRepository.persist(user);
        
        logger.info("Estado del usuario " + id + " cambiado exitosamente a: " + status);
        return true;
    }
    
    /**
     * Desactiva un usuario (cambio de estado a INACTIVE)
     * @param id ID del usuario
     * @return true si se desactivó correctamente, false si no existe
     */
    @Transactional
    public boolean deactivateUser(Long id) {
        logger.info("Desactivando usuario con ID: " + id);
        return changeUserStatus(id, UserStatus.INACTIVE);
    }
    
    /**
     * Activa un usuario (cambio de estado a ACTIVE)
     * @param id ID del usuario
     * @return true si se activó correctamente, false si no existe
     */
    @Transactional
    public boolean activateUser(Long id) {
        logger.info("Activando usuario con ID: " + id);
        return changeUserStatus(id, UserStatus.ACTIVE);
    }
    
    /**
     * Suspende un usuario (cambio de estado a SUSPENDED)
     * @param id ID del usuario
     * @return true si se suspendió correctamente, false si no existe
     */
    @Transactional
    public boolean suspendUser(Long id) {
        logger.info("Suspendiendo usuario con ID: " + id);
        return changeUserStatus(id, UserStatus.SUSPENDED);
    }
    
    /**
     * Sincroniza un usuario con Keycloak
     * @param keycloakId ID de Keycloak
     * @return Optional con UserResponseDTO si existe
     */
    public Optional<UserResponseDTO> syncWithKeycloak(String keycloakId) {
        logger.info("Sincronizando usuario con Keycloak ID: " + keycloakId);
        // Implementación futura para sincronización con Keycloak
        return getUserByKeycloakId(keycloakId);
    }
    
    
    /**
     * Convierte una entidad User a UserResponseDTO
     * @param user Entidad User
     * @return UserResponseDTO
     */
    private UserResponseDTO toDTO(User user) {
        CompanyDTO companyDTO = new CompanyDTO(
                user.company.getId(),
                user.company.getName(),
                new CountryDTO(
                        user.company.getCountry().getId(),
                        user.company.getCountry().getIsoCode(),
                        user.company.getCountry().getName()
                ),
                user.company.getAddress()
        );
        
        CountryDTO countryDTO = new CountryDTO(
                user.country.getId(),
                user.country.getIsoCode(),
                user.country.getName()
        );
        
        return new UserResponseDTO(
                user.id,
                user.email,
                user.name,
                user.keycloakId,
                user.role,
                companyDTO,
                countryDTO,
                user.status,
                user.createdAt,
                user.updatedAt
        );
    }
}