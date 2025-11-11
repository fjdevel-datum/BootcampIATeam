package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CreateUserRequest;
import com.datum.redsoft.dto.request.UpdateUserRequest;
import com.datum.redsoft.dto.response.UserResponseDTO;
import com.datum.redsoft.enums.UserStatus;
import com.datum.redsoft.service.interfaces.IUserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controlador REST para la gestión de usuarios
 * Proporciona endpoints para todas las operaciones CRUD de usuarios
 */
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    
    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    
    @Inject
    IUserService userService;
    
    /**
     * Obtiene todos los usuarios
     * GET /api/users
     */
    @GET
    public Response getAllUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            logger.severe("Error al obtener usuarios: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene un usuario por ID
     * GET /api/users/{id}
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            Optional<UserResponseDTO> user = userService.getUserById(id);
            
            if (user.isEmpty()) {
                return Response.status(404)
                        .entity("Usuario no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok(user.get()).build();
        } catch (Exception e) {
            logger.severe("Error al obtener usuario por ID: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene un usuario por email
     * GET /api/users/email/{email}
     */
    @GET
    @Path("/email/{email}")
    public Response getUserByEmail(@PathParam("email") String email) {
        try {
            Optional<UserResponseDTO> user = userService.getUserByEmail(email);
            
            if (user.isEmpty()) {
                return Response.status(404)
                        .entity("Usuario no encontrado con email: " + email)
                        .build();
            }
            
            return Response.ok(user.get()).build();
        } catch (Exception e) {
            logger.severe("Error al obtener usuario por email: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene un usuario por Keycloak ID
     * GET /api/users/keycloak/{keycloakId}
     */
    @GET
    @Path("/keycloak/{keycloakId}")
    public Response getUserByKeycloakId(@PathParam("keycloakId") String keycloakId) {
        try {
            Optional<UserResponseDTO> user = userService.getUserByKeycloakId(keycloakId);
            
            if (user.isEmpty()) {
                return Response.status(404)
                        .entity("Usuario no encontrado con Keycloak ID: " + keycloakId)
                        .build();
            }
            
            return Response.ok(user.get()).build();
        } catch (Exception e) {
            logger.severe("Error al obtener usuario por Keycloak ID: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene usuarios por empresa
     * GET /api/users/company/{companyId}
     */
    @GET
    @Path("/company/{companyId}")
    public Response getUsersByCompany(@PathParam("companyId") Long companyId) {
        try {
            List<UserResponseDTO> users = userService.getUsersByCompany(companyId);
            return Response.ok(users).build();
        } catch (Exception e) {
            logger.severe("Error al obtener usuarios por empresa: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene solo usuarios activos
     * GET /api/users/active
     */
    @GET
    @Path("/active")
    public Response getActiveUsers() {
        try {
            List<UserResponseDTO> users = userService.getActiveUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            logger.severe("Error al obtener usuarios activos: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Busca usuarios por nombre
     * GET /api/users/search?name={name}
     */
    @GET
    @Path("/search")
    public Response searchUsersByName(@QueryParam("name") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return Response.status(400)
                        .entity("El parámetro 'name' es obligatorio")
                        .build();
            }
            
            List<UserResponseDTO> users = userService.searchUsersByName(name);
            return Response.ok(users).build();
        } catch (Exception e) {
            logger.severe("Error al buscar usuarios por nombre: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Crea un nuevo usuario
     * POST /api/users
     */
    @POST
    public Response createUser(@Valid CreateUserRequest request) {
        try {
            UserResponseDTO user = userService.createUser(request);
            return Response.status(201).entity(user).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al crear usuario: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al crear usuario: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Actualiza un usuario existente
     * PUT /api/users/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, @Valid UpdateUserRequest request) {
        try {
            Optional<UserResponseDTO> updatedUser = userService.updateUser(id, request);
            
            if (updatedUser.isEmpty()) {
                return Response.status(404)
                        .entity("Usuario no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok(updatedUser.get()).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al actualizar usuario: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al actualizar usuario: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Cambia el estado de un usuario
     * PATCH /api/users/{id}/status/{status}
     */
    @PATCH
    @Path("/{id}/status/{status}")
    public Response changeUserStatus(@PathParam("id") Long id, @PathParam("status") UserStatus status) {
        try {
            boolean success = userService.changeUserStatus(id, status);
            
            if (!success) {
                return Response.status(404)
                        .entity("Usuario no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Estado del usuario actualizado correctamente a: " + status.getDisplayName())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al cambiar estado del usuario: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Desactiva un usuario (soft delete)
     * DELETE /api/users/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deactivateUser(@PathParam("id") Long id) {
        try {
            boolean success = userService.deactivateUser(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Usuario no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Usuario desactivado correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al desactivar usuario: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Activa un usuario
     * PATCH /api/users/{id}/activate
     */
    @PATCH
    @Path("/{id}/activate")
    public Response activateUser(@PathParam("id") Long id) {
        try {
            boolean success = userService.activateUser(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Usuario no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Usuario activado correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al activar usuario: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Suspende un usuario
     * PATCH /api/users/{id}/suspend
     */
    @PATCH
    @Path("/{id}/suspend")
    public Response suspendUser(@PathParam("id") Long id) {
        try {
            boolean success = userService.suspendUser(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Usuario no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Usuario suspendido correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al suspender usuario: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    
    /**
     * Sincroniza usuario con Keycloak
     * POST /api/users/sync-keycloak/{keycloakId}
     */
    @POST
    @Path("/sync-keycloak/{keycloakId}")
    public Response syncWithKeycloak(@PathParam("keycloakId") String keycloakId) {
        try {
            Optional<UserResponseDTO> user = userService.syncWithKeycloak(keycloakId);
            
            if (user.isEmpty()) {
                return Response.status(404)
                        .entity("Usuario no encontrado con Keycloak ID: " + keycloakId)
                        .build();
            }
            
            return Response.ok(user.get()).build();
        } catch (Exception e) {
            logger.severe("Error al sincronizar con Keycloak: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
   
}