# Plan de Implementación - Sistema de Gestión de Gastos Corporativos
**Proyecto:** OCR-FILE (Java + Quarkus)  
**Fecha:** 5 de octubre de 2025  
**Tecnología:** Java 21, Quarkus 3.28.1, Oracle Database, Hibernate ORM with Panache

---

## 📋 RESUMEN EJECUTIVO

### Estado Actual
- ✅ Proyecto base configurado con Quarkus
- ✅ Entidades `Company` y `Country` implementadas
- ✅ Servicios `CompanyService` y `CountryService` funcionando
- ✅ Integración con Azure Document Intelligence
- ✅ Servicio `LlamaInvoiceExtractionService` operativo
- ✅ Base de datos Oracle configurada

### Objetivos
1. **Fase 1:** Refactorizar servicios existentes (interfaces y limpieza)
2. **Fase 2:** Implementar 7 nuevas entidades del sistema de gastos
3. **Fase 3:** Crear servicios, repositorios y DTOs correspondientes
4. **Fase 4:** Implementar controladores REST y validaciones
5. **Fase 5:** Testing e integración completa

---

## 🏗️ FASE 1: REFACTORIZACIÓN DE SERVICIOS EXISTENTES
**Duración:** 1-2 días  
**Prioridad:** ALTA

### ✅ Checkpoint 1.1: Crear Interfaces para Company y Country Services

#### 1.1.1 Crear Interface ICompanyService
```bash
# Ubicación: src/main/java/com/datum/redsoft/service/interfaces/ICompanyService.java
```

**Contenido del archivo:**
```java
package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.request.CompanyCreateRequest;
import com.datum.redsoft.dto.request.CompanyUpdateRequest;
import com.datum.redsoft.dto.response.CompanyDTO;

import java.util.List;
import java.util.Optional;

public interface ICompanyService {
    List<CompanyDTO> getAllCompanies();
    Optional<CompanyDTO> getCompanyById(Long id);
    List<CompanyDTO> getCompaniesByCountryId(Long countryId);
    List<CompanyDTO> searchCompaniesByName(String name);
    CompanyDTO createCompany(CompanyCreateRequest request);
    Optional<CompanyDTO> updateCompany(Long id, CompanyUpdateRequest request);
    boolean deleteCompany(Long id);
}
```

**Acciones:**
- [ ] Crear archivo de interface
- [ ] Modificar `CompanyService` para implementar `ICompanyService`
- [ ] Actualizar controladores para usar la interface

#### 1.1.2 Crear Interface ICountryService
```bash
# Ubicación: src/main/java/com/datum/redsoft/service/interfaces/ICountryService.java
```

**Contenido del archivo:**
```java
package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.entity.Country;
import com.datum.redsoft.dto.response.CountryDTO;

import java.util.List;
import java.util.Optional;

public interface ICountryService {
    List<CountryDTO> getAllCountries();
    Optional<CountryDTO> getCountryById(Long id);
    Optional<Country> getCountryEntityById(Long id);
    Optional<CountryDTO> getCountryByCode(String isoCode);
}
```

**Acciones:**
- [ ] Crear archivo de interface
- [ ] Modificar `CountryService` para implementar `ICountryService`
- [ ] Verificar que métodos existentes estén alineados con la interface

### ✅ Checkpoint 1.2: Refactorizar LlamaInvoiceExtractionService

#### 1.2.1 Eliminar método extractWithRegexFallback
**Ubicación:** `src/main/java/com/datum/redsoft/service/LlamaInvoiceExtractionService.java`

**Cambios a realizar:**
1. **Eliminar método completo** `extractWithRegexFallback` (líneas 75-143)
2. **Eliminar métodos auxiliares:**
   - `extractAmount` (líneas 145-164)
   - `extractDate` (líneas 166-181)
   - `extractInvoiceNumber` (líneas 183-204)
3. **Modificar método principal** `extractInvoiceData` para manejo de errores mejorado
4. **Mejorar logging** y manejo de excepciones

**Código modificado para extractInvoiceData:**
```java
@Override
public InvoiceDataResponse extractInvoiceData(String extractedText) throws InvoiceExtractionException {
    try {
        logger.info("Iniciando extracción de datos de factura con Llama AI");
        
        // Validar entrada
        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new InvoiceExtractionException("El texto extraído está vacío o es nulo");
        }
        
        // Crear el prompt específico para extraer información de facturas
        String prompt = createInvoiceExtractionPrompt(extractedText);
        
        // Preparar la petición a Llama
        LlamaRequest request = createLlamaRequest(prompt);
        
        // Hacer la petición HTTP con retry automático
        String jsonResponse = makeHttpRequestWithRetry(request);
        
        // Procesar la respuesta y crear el DTO
        InvoiceDataResponse response = parseInvoiceResponse(jsonResponse);
        response.setExtractionMethod("AI");
        response.setConfidenceScore(0.85);
        
        logger.info("Extracción AI completada exitosamente");
        return response;
        
    } catch (Exception e) {
        logger.severe("Error en extracción AI: " + e.getMessage());
        throw new InvoiceExtractionException("Error al extraer datos de la factura", e);
    }
}
```

**Agregar método de retry:**
```java
private String makeHttpRequestWithRetry(LlamaRequest request) throws Exception {
    int maxRetries = hfConfig.getRetryMaxAttempts();
    int delayMs = hfConfig.getRetryDelayMs();
    
    Exception lastException = null;
    
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
        try {
            return makeHttpRequest(request);
        } catch (Exception e) {
            lastException = e;
            logger.warning($"Intento {attempt}/{maxRetries} falló: {e.getMessage()}");
            
            if (attempt < maxRetries) {
                Thread.sleep(delayMs * attempt); // Backoff exponencial
            }
        }
    }
    
    throw new InvoiceExtractionException("Falló después de " + maxRetries + " intentos", lastException);
}
```

**Acciones:**
- [ ] Eliminar métodos indicados
- [ ] Implementar método de retry
- [ ] Mejorar manejo de errores
- [ ] Actualizar tests unitarios
- [ ] Validar con facturas reales

---

## 🏗️ FASE 2: IMPLEMENTACIÓN DE ENTIDADES CORE
**Duración:** 3-4 días  
**Prioridad:** ALTA

### ✅ Checkpoint 2.1: Implementar Entidad User

#### 2.1.1 Crear Enums requeridos
```bash
# Ubicación: src/main/java/com/datum/redsoft/enums/
```

**UserRole.java:**
```java
package com.datum.redsoft.enums;

public enum UserRole {
    COLLABORATOR("Colaborador"),
    ADMIN("Administrador");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

**UserStatus.java:**
```java
package com.datum.redsoft.enums;

public enum UserStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    SUSPENDED("Suspendido");
    
    private final String displayName;
    
    UserStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

#### 2.1.2 Crear Entidad User
```bash
# Ubicación: src/main/java/com/datum/redsoft/entity/User.java
```

**Contenido:**
```java
package com.datum.redsoft.entity;

import com.datum.redsoft.enums.UserRole;
import com.datum.redsoft.enums.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "keycloak_id")
       })
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "users_seq", allocationSize = 1)
    public Long id;
    
    @Column(unique = true, nullable = false)
    @Email(message = "Email debe tener formato válido")
    @NotBlank(message = "Email es obligatorio")
    public String email;
    
    @Column(nullable = false)
    @NotBlank(message = "Nombre es obligatorio")
    public String name;
    
    @Column(name = "keycloak_id", unique = true, nullable = false)
    @NotBlank(message = "Keycloak ID es obligatorio")
    public String keycloakId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Rol es obligatorio")
    public UserRole role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull(message = "Empresa es obligatoria")
    public Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @NotNull(message = "País es obligatorio")
    public Country country;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserStatus status = UserStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
```

#### 2.1.3 Crear DTOs para User
```bash
# Ubicación: src/main/java/com/datum/redsoft/dto/request/ y dto/response/
```

**CreateUserRequest.java:**
```java
package com.datum.redsoft.dto.request;

import com.datum.redsoft.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
    
    @Email(message = "Email debe tener formato válido")
    @NotBlank(message = "Email es obligatorio")
    private String email;
    
    @NotBlank(message = "Nombre es obligatorio")
    private String name;
    
    @NotBlank(message = "Keycloak ID es obligatorio")
    private String keycloakId;
    
    @NotNull(message = "Rol es obligatorio")
    private UserRole role;
    
    @NotNull(message = "ID de empresa es obligatorio")
    private Long companyId;
    
    @NotNull(message = "ID de país es obligatorio")
    private Long countryId;
}
```

**UpdateUserRequest.java:**
```java
package com.datum.redsoft.dto.request;

import com.datum.redsoft.enums.UserRole;
import com.datum.redsoft.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    
    @Email(message = "Email debe tener formato válido")
    private String email;
    
    private String name;
    
    private UserRole role;
    
    private Long companyId;
    
    private Long countryId;
    
    private UserStatus status;
}
```

**UserResponseDTO.java:**
```java
package com.datum.redsoft.dto.response;

import com.datum.redsoft.enums.UserRole;
import com.datum.redsoft.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String keycloakId;
    private UserRole role;
    private CompanyDTO company;
    private CountryDTO country;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### 2.1.4 Crear UserRepository
```bash
# Ubicación: src/main/java/com/datum/redsoft/repository/UserRepository.java
```

**Contenido:**
```java
package com.datum.redsoft.repository;

import com.datum.redsoft.entity.User;
import com.datum.redsoft.enums.UserRole;
import com.datum.redsoft.enums.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    
    public Optional<User> findByKeycloakId(String keycloakId) {
        return find("keycloakId", keycloakId).firstResultOptional();
    }
    
    public List<User> findByCompanyId(Long companyId) {
        return find("company.id", companyId).list();
    }
    
    public List<User> findByRole(UserRole role) {
        return find("role", role).list();
    }
    
    public List<User> findActiveUsers() {
        return find("status", UserStatus.ACTIVE).list();
    }
    
    public List<User> findByCompanyIdAndStatus(Long companyId, UserStatus status) {
        return find("company.id = ?1 and status = ?2", companyId, status).list();
    }
    
    public Optional<User> findByIdWithRelations(Long id) {
        return find("select u from User u left join fetch u.company c left join fetch c.country left join fetch u.country where u.id = ?1", id)
                .firstResultOptional();
    }
    
    public List<User> findAllWithRelations() {
        return find("select u from User u left join fetch u.company c left join fetch c.country left join fetch u.country")
                .list();
    }
}
```

#### 2.1.5 Crear UserService
```bash
# Ubicación: src/main/java/com/datum/redsoft/service/UserService.java
```

**Contenido:**
```java
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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {
    
    @Inject
    UserRepository userRepository;
    
    @Inject
    CompanyRepository companyRepository;
    
    @Inject
    CountryRepository countryRepository;
    
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAllWithRelations()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findByIdWithRelations(id)
                .map(this::toDTO);
    }
    
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toDTO);
    }
    
    public Optional<UserResponseDTO> getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(this::toDTO);
    }
    
    public List<UserResponseDTO> getUsersByCompany(Long companyId) {
        return userRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserResponseDTO createUser(CreateUserRequest request) {
        // Validar que email no esté en uso
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con este email");
        }
        
        // Validar que keycloakId no esté en uso
        if (userRepository.findByKeycloakId(request.getKeycloakId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con este Keycloak ID");
        }
        
        // Validar que empresa existe
        Company company = companyRepository.findByIdOptional(request.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa con ID: " + request.getCompanyId()));
        
        // Validar que país existe
        Country country = countryRepository.findByIdOptional(request.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el país con ID: " + request.getCountryId()));
        
        User user = new User();
        user.email = request.getEmail();
        user.name = request.getName();
        user.keycloakId = request.getKeycloakId();
        user.role = request.getRole();
        user.company = company;
        user.country = country;
        user.status = UserStatus.ACTIVE;
        
        userRepository.persist(user);
        return toDTO(user);
    }
    
    @Transactional
    public Optional<UserResponseDTO> updateUser(Long id, UpdateUserRequest request) {
        Optional<User> userOpt = userRepository.findByIdWithRelations(id);
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        if (request.getEmail() != null && !request.getEmail().equals(user.email)) {
            // Validar que el nuevo email no esté en uso
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un usuario con este email");
            }
            user.email = request.getEmail();
        }
        
        if (request.getName() != null) {
            user.name = request.getName();
        }
        
        if (request.getRole() != null) {
            user.role = request.getRole();
        }
        
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findByIdOptional(request.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa con ID: " + request.getCompanyId()));
            user.company = company;
        }
        
        if (request.getCountryId() != null) {
            Country country = countryRepository.findByIdOptional(request.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el país con ID: " + request.getCountryId()));
            user.country = country;
        }
        
        if (request.getStatus() != null) {
            user.status = request.getStatus();
        }
        
        userRepository.persist(user);
        return Optional.of(toDTO(user));
    }
    
    @Transactional
    public boolean changeUserStatus(Long id, UserStatus status) {
        Optional<User> userOpt = userRepository.findByIdOptional(id);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        user.status = status;
        userRepository.persist(user);
        return true;
    }
    
    @Transactional
    public boolean deactivateUser(Long id) {
        return changeUserStatus(id, UserStatus.INACTIVE);
    }
    
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
```

#### 2.1.6 Crear UserController
```bash
# Ubicación: src/main/java/com/datum/redsoft/controller/UserController.java
```

**Contenido:**
```java
package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CreateUserRequest;
import com.datum.redsoft.dto.request.UpdateUserRequest;
import com.datum.redsoft.dto.response.UserResponseDTO;
import com.datum.redsoft.enums.UserStatus;
import com.datum.redsoft.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    
    @Inject
    UserService userService;
    
    @GET
    public Response getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return Response.ok(users).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        Optional<UserResponseDTO> user = userService.getUserById(id);
        
        if (user.isEmpty()) {
            return Response.status(404)
                    .entity("Usuario no encontrado")
                    .build();
        }
        
        return Response.ok(user.get()).build();
    }
    
    @GET
    @Path("/email/{email}")
    public Response getUserByEmail(@PathParam("email") String email) {
        Optional<UserResponseDTO> user = userService.getUserByEmail(email);
        
        if (user.isEmpty()) {
            return Response.status(404)
                    .entity("Usuario no encontrado")
                    .build();
        }
        
        return Response.ok(user.get()).build();
    }
    
    @GET
    @Path("/keycloak/{keycloakId}")
    public Response getUserByKeycloakId(@PathParam("keycloakId") String keycloakId) {
        Optional<UserResponseDTO> user = userService.getUserByKeycloakId(keycloakId);
        
        if (user.isEmpty()) {
            return Response.status(404)
                    .entity("Usuario no encontrado")
                    .build();
        }
        
        return Response.ok(user.get()).build();
    }
    
    @GET
    @Path("/company/{companyId}")
    public Response getUsersByCompany(@PathParam("companyId") Long companyId) {
        List<UserResponseDTO> users = userService.getUsersByCompany(companyId);
        return Response.ok(users).build();
    }
    
    @POST
    public Response createUser(@Valid CreateUserRequest request) {
        try {
            UserResponseDTO user = userService.createUser(request);
            return Response.status(201).entity(user).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, @Valid UpdateUserRequest request) {
        try {
            Optional<UserResponseDTO> updatedUser = userService.updateUser(id, request);
            
            if (updatedUser.isEmpty()) {
                return Response.status(404)
                        .entity("Usuario no encontrado")
                        .build();
            }
            
            return Response.ok(updatedUser.get()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/status/{status}")
    public Response changeUserStatus(@PathParam("id") Long id, @PathParam("status") UserStatus status) {
        boolean success = userService.changeUserStatus(id, status);
        
        if (!success) {
            return Response.status(404)
                    .entity("Usuario no encontrado")
                    .build();
        }
        
        return Response.ok()
                .entity("Estado del usuario actualizado correctamente")
                .build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deactivateUser(@PathParam("id") Long id) {
        boolean success = userService.deactivateUser(id);
        
        if (!success) {
            return Response.status(404)
                    .entity("Usuario no encontrado")
                    .build();
        }
        
        return Response.ok()
                .entity("Usuario desactivado correctamente")
                .build();
    }
}
```

### ✅ Checkpoint 2.2: Implementar Entidad Card

#### 2.2.1 Crear Enum CardStatus
```bash
# Ubicación: src/main/java/com/datum/redsoft/enums/CardStatus.java
```

**Contenido:**
```java
package com.datum.redsoft.enums;

public enum CardStatus {
    ACTIVE("Activa"),
    BLOCKED("Bloqueada"),
    EXPIRED("Expirada"),
    CANCELLED("Cancelada");
    
    private final String displayName;
    
    CardStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

#### 2.2.2 Crear Entidad Card
```bash
# Ubicación: src/main/java/com/datum/redsoft/entity/Card.java
```

**Contenido:**
```java
package com.datum.redsoft.entity;

import com.datum.redsoft.enums.CardStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Data
@EqualsAndHashCode(callSuper = false)
public class Card extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq")
    @SequenceGenerator(name = "card_seq", sequenceName = "cards_seq", allocationSize = 1)
    public Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuario es obligatorio")
    public User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull(message = "Empresa es obligatoria")
    public Company company;
    
    @Column(name = "card_mask", nullable = false)
    @NotBlank(message = "Máscara de tarjeta es obligatoria")
    @Pattern(regexp = "\\*{4} \\*{4} \\*{4} \\d{4}", 
             message = "El formato de la máscara debe ser **** **** **** XXXX")
    public String cardMask;
    
    @Column(name = "credit_limit", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.01", message = "El límite de crédito debe ser mayor a 0")
    @NotNull(message = "Límite de crédito es obligatorio")
    public BigDecimal creditLimit;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CardStatus status = CardStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
```

#### 2.2.3 Crear DTOs para Card
**CreateCardRequest.java:**
```java
package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCardRequest {
    
    @NotNull(message = "ID de usuario es obligatorio")
    private Long userId;
    
    @NotNull(message = "ID de empresa es obligatorio")
    private Long companyId;
    
    @NotBlank(message = "Número de tarjeta es obligatorio")
    private String cardNumber; // Se convertirá a mask internamente
    
    @DecimalMin(value = "0.01", message = "El límite de crédito debe ser mayor a 0")
    @NotNull(message = "Límite de crédito es obligatorio")
    private BigDecimal creditLimit;
}
```

**CardResponseDTO.java:**
```java
package com.datum.redsoft.dto.response;

import com.datum.redsoft.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CardResponseDTO {
    private Long id;
    private UserResponseDTO user;
    private CompanyDTO company;
    private String cardMask;
    private BigDecimal creditLimit;
    private CardStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### 2.2.4 Crear CardRepository
```java
package com.datum.redsoft.repository;

import com.datum.redsoft.entity.Card;
import com.datum.redsoft.enums.CardStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {
    
    public List<Card> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }
    
    public List<Card> findByCompanyId(Long companyId) {
        return find("company.id", companyId).list();
    }
    
    public List<Card> findActiveCardsByUser(Long userId) {
        return find("user.id = ?1 and status = ?2", userId, CardStatus.ACTIVE).list();
    }
    
    public List<Card> findActiveCardsByCompany(Long companyId) {
        return find("company.id = ?1 and status = ?2", companyId, CardStatus.ACTIVE).list();
    }
    
    public Optional<Card> findByIdWithRelations(Long id) {
        return find("select c from Card c left join fetch c.user u left join fetch u.company left join fetch c.company co left join fetch co.country where c.id = ?1", id)
                .firstResultOptional();
    }
    
    public List<Card> findAllWithRelations() {
        return find("select c from Card c left join fetch c.user u left join fetch u.company left join fetch c.company co left join fetch co.country")
                .list();
    }
}
```

### ✅ Checkpoint 2.3: Implementar Entidades Invoice e InvoiceField

#### 2.3.1 Crear Enum InvoiceStatus
```java
package com.datum.redsoft.enums;

public enum InvoiceStatus {
    DRAFT("Borrador"),
    PENDING("Pendiente"),
    VERIFIED("Verificado"),
    REJECTED("Rechazado");
    
    private final String displayName;
    
    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

#### 2.3.2 Crear Entidad Invoice
```java
package com.datum.redsoft.entity;

import com.datum.redsoft.enums.InvoiceStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@EqualsAndHashCode(callSuper = false)
public class Invoice extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_seq")
    @SequenceGenerator(name = "invoice_seq", sequenceName = "invoices_seq", allocationSize = 1)
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
    
    @Column(name = "original_file_url", nullable = false)
    @NotBlank(message = "URL del archivo original es obligatoria")
    public String originalFileUrl;
    
    @Column(name = "thumbnail_url")
    public String thumbnailUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
```

#### 2.3.3 Crear Entidad InvoiceField
```java
package com.datum.redsoft.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_fields")
@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceField extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_field_seq")
    @SequenceGenerator(name = "invoice_field_seq", sequenceName = "invoice_fields_seq", allocationSize = 1)
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
    
    @Column(columnDefinition = "TEXT", nullable = false)
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
    
    @Column(columnDefinition = "TEXT")
    public String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
```

---

## 🏗️ FASE 3: IMPLEMENTACIÓN DE ENTIDADES DE CATÁLOGO
**Duración:** 2-3 días  
**Prioridad:** MEDIA

### ✅ Checkpoint 3.1: Implementar Category y CostCenter

#### 3.1.1 Crear Entidad Category
```java
package com.datum.redsoft.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@EqualsAndHashCode(callSuper = false)
public class Category extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "categories_seq", allocationSize = 1)
    public Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Nombre de categoría es obligatorio")
    public String name;
    
    @Column(columnDefinition = "TEXT")
    public String description;
    
    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
```

#### 3.1.2 Crear Entidad CostCenter
```java
package com.datum.redsoft.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cost_centers", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Data
@EqualsAndHashCode(callSuper = false)
public class CostCenter extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cost_center_seq")
    @SequenceGenerator(name = "cost_center_seq", sequenceName = "cost_centers_seq", allocationSize = 1)
    public Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Código de centro de costo es obligatorio")
    public String code;
    
    @Column(nullable = false)
    @NotBlank(message = "Descripción es obligatoria")
    public String description;
    
    @Column(columnDefinition = "TEXT")
    public String usage;
    
    public String responsible;
    
    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
```

---

## 🏗️ FASE 4: IMPLEMENTACIÓN DE REPORTERÍA
**Duración:** 3-4 días  
**Prioridad:** MEDIA-ALTA

### ✅ Checkpoint 4.1: Implementar Report y ReportInvoice

#### 4.1.1 Crear Enum ReportStatus
```java
package com.datum.redsoft.enums;

public enum ReportStatus {
    DRAFT("Borrador"),
    SUBMITTED("Enviado"),
    APPROVED("Aprobado"),
    REJECTED("Rechazado");
    
    private final String displayName;
    
    ReportStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

#### 4.1.2 Crear Entidad Report
```java
package com.datum.redsoft.entity;

import com.datum.redsoft.enums.ReportStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reports")
@Data
@EqualsAndHashCode(callSuper = false)
public class Report extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_seq")
    @SequenceGenerator(name = "report_seq", sequenceName = "reports_seq", allocationSize = 1)
    public Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuario es obligatorio")
    public User user;
    
    @Column(name = "period_start", nullable = false)
    @NotNull(message = "Fecha de inicio del período es obligatoria")
    public LocalDate periodStart;
    
    @Column(name = "period_end", nullable = false)
    @NotNull(message = "Fecha de fin del período es obligatoria")
    public LocalDate periodEnd;
    
    @Column(name = "file_url")
    public String fileUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ReportStatus status = ReportStatus.DRAFT;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "report_invoices",
        joinColumns = @JoinColumn(name = "report_id"),
        inverseJoinColumns = @JoinColumn(name = "invoice_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"report_id", "invoice_id"})
    )
    public Set<Invoice> invoices = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
```

---

## 🏗️ FASE 5: TESTING E INTEGRACIÓN
**Duración:** 2-3 días  
**Prioridad:** ALTA

### ✅ Checkpoint 5.1: Tests Unitarios y de Integración

#### 5.1.1 Estructura de tests
```bash
src/test/java/com/datum/redsoft/
├── service/
│   ├── UserServiceTest.java
│   ├── CardServiceTest.java
│   └── LlamaInvoiceExtractionServiceTest.java
├── controller/
│   ├── UserControllerTest.java
│   └── CardControllerTest.java
└── repository/
    ├── UserRepositoryTest.java
    └── CardRepositoryTest.java
```

#### 5.1.2 Configuración de test
**application-test.properties:**
```properties
# Base de datos en memoria para tests
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.log.sql=false

# Configuraciones mock para Azure y HuggingFace
azure.document-intelligence.endpoint=http://localhost:8080/mock-azure
azure.document-intelligence.key=mock-key
huggingface.token=mock-token
huggingface.api.url=http://localhost:8080/mock-hf
```

### ✅ Checkpoint 5.2: Migraciones de Base de Datos

#### 5.2.1 Scripts SQL para Oracle
```bash
# Ubicación: src/main/resources/db/migration/
```

**V1.1__create_users_table.sql:**
```sql
-- Crear secuencia para users
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;

-- Crear tabla users
CREATE TABLE users (
    id NUMBER(19) PRIMARY KEY,
    email VARCHAR2(255) NOT NULL UNIQUE,
    name VARCHAR2(255) NOT NULL,
    keycloak_id VARCHAR2(255) NOT NULL UNIQUE,
    role VARCHAR2(50) NOT NULL CHECK (role IN ('COLLABORATOR', 'ADMIN')),
    company_id NUMBER(19) NOT NULL,
    country_id NUMBER(19) NOT NULL,
    status VARCHAR2(50) DEFAULT 'ACTIVE' NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_users_country FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- Índices para optimización
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_keycloak_id ON users(keycloak_id);
CREATE INDEX idx_users_company_id ON users(company_id);
CREATE INDEX idx_users_status ON users(status);
```

**V1.2__create_cards_table.sql:**
```sql
-- Crear secuencia para cards
CREATE SEQUENCE cards_seq START WITH 1 INCREMENT BY 1;

-- Crear tabla cards
CREATE TABLE cards (
    id NUMBER(19) PRIMARY KEY,
    user_id NUMBER(19) NOT NULL,
    company_id NUMBER(19) NOT NULL,
    card_mask VARCHAR2(19) NOT NULL,
    credit_limit NUMBER(15,2) NOT NULL CHECK (credit_limit > 0),
    status VARCHAR2(50) DEFAULT 'ACTIVE' NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cards_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_cards_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- Índices
CREATE INDEX idx_cards_user_id ON cards(user_id);
CREATE INDEX idx_cards_company_id ON cards(company_id);
CREATE INDEX idx_cards_status ON cards(status);
```

### ✅ Checkpoint 5.3: Documentación

#### 5.3.1 Actualizar README.md
Agregar secciones sobre:
- Nuevas entidades implementadas
- Endpoints disponibles
- Variables de entorno requeridas
- Guía de desarrollo

#### 5.3.2 Documentación API con OpenAPI
Agregar dependency en pom.xml:
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
```

---

## 📋 CHECKLIST DE IMPLEMENTACIÓN

### Fase 1 - Refactorización
- [ ] Crear interfaces ICompanyService e ICountryService
- [ ] Implementar interfaces en servicios existentes
- [ ] Eliminar extractWithRegexFallback de LlamaInvoiceExtractionService
- [ ] Mejorar manejo de errores con retry
- [ ] Actualizar tests existentes

### Fase 2 - Entidades Core
- [ ] Crear enums (UserRole, UserStatus, CardStatus, InvoiceStatus)
- [ ] Implementar entidad User con validaciones
- [ ] Crear DTOs para User (Create, Update, Response)
- [ ] Implementar UserRepository con queries personalizados
- [ ] Desarrollar UserService con lógica de negocio
- [ ] Crear UserController con endpoints REST
- [ ] Repetir proceso para Card, Invoice, InvoiceField

### Fase 3 - Entidades Catálogo
- [ ] Implementar Category con validaciones
- [ ] Implementar CostCenter con validaciones
- [ ] Crear servicios y repositorios correspondientes
- [ ] Desarrollar endpoints REST

### Fase 4 - Reportería
- [ ] Crear enum ReportStatus
- [ ] Implementar entidad Report con relaciones N:M
- [ ] Desarrollar lógica de generación de reportes
- [ ] Implementar workflow de aprobación

### Fase 5 - Testing e Integración
- [ ] Configurar ambiente de testing
- [ ] Crear tests unitarios para servicios críticos
- [ ] Implementar tests de integración para APIs
- [ ] Crear migraciones SQL para Oracle
- [ ] Documentar API con OpenAPI
- [ ] Actualizar documentación del proyecto

---

## 🚀 COMANDOS DE EJECUCIÓN

### Desarrollo
```bash
# Modo desarrollo con hot reload
./mvnw quarkus:dev

# Ejecutar tests
./mvnw test

# Ejecutar tests específicos
./mvnw test -Dtest=UserServiceTest

# Generar reporte de cobertura
./mvnw jacoco:report
```

### Producción
```bash
# Compilar aplicación
./mvnw package

# Ejecutar aplicación
java -jar target/quarkus-app/quarkus-run.jar

# Compilar imagen nativa
./mvnw package -Pnative
```

### Base de Datos
```bash
# Conectar a Oracle (requiere cliente)
sqlplus ${DB_USERNAME}/${DB_PASSWORD}@${DB_HOST}:${DB_PORT}/${DB_SERVICE}

# Ejecutar migraciones (con Flyway si se configura)
./mvnw flyway:migrate
```

---

## 🔧 CONFIGURACIÓN REQUERIDA

### Variables de Entorno Adicionales
```bash
# Base de datos
DB_USERNAME=ocr_user
DB_PASSWORD=secure_password
DB_URL=jdbc:oracle:thin:@localhost:1521:XE

# Keycloak (para implementación futura)
KEYCLOAK_URL=http://localhost:8080/auth
KEYCLOAK_REALM=ocr-realm
KEYCLOAK_CLIENT_ID=ocr-client
KEYCLOAK_CLIENT_SECRET=client-secret

# Almacenamiento de archivos (Azure Blob o S3)
STORAGE_ACCOUNT_NAME=ocrstorageaccount
STORAGE_ACCOUNT_KEY=storage-key
STORAGE_CONTAINER_NAME=invoices

# Configuración de email (para notificaciones)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=notifications@company.com
SMTP_PASSWORD=email-password
```

### Dependencias Adicionales en pom.xml
```xml
<!-- OpenAPI Documentation -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>

<!-- Email notifications -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-mailer</artifactId>
</dependency>

<!-- File upload/download -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-reactive-multipart</artifactId>
</dependency>

<!-- Keycloak OIDC -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-oidc</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5-mockito</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 🎯 PRÓXIMOS PASOS DESPUÉS DE LA IMPLEMENTACIÓN

1. **Integración con Keycloak** para autenticación
2. **Implementación de almacenamiento** de archivos (Azure Blob/S3)
3. **Sistema de notificaciones** por email
4. **Dashboard** administrativo
5. **Reportes avanzados** con filtros complejos
6. **API de integración** con sistemas contables
7. **Aplicación móvil** para captura de recibos

---

**Tiempo total estimado:** 10-14 días  
**Complejidad:** Media-Alta  
**Recursos necesarios:** 1-2 desarrolladores senior Java
