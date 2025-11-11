package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CreateCategoryRequest;
import com.datum.redsoft.dto.request.UpdateCategoryRequest;
import com.datum.redsoft.dto.response.CategoryResponseDTO;
import com.datum.redsoft.service.interfaces.ICategoryService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controlador REST para la gestión de categorías
 * Proporciona endpoints para todas las operaciones CRUD de categorías
 */
@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryController {
    
    private static final Logger logger = Logger.getLogger(CategoryController.class.getName());
    
    @Inject
    ICategoryService categoryService;
    
    /**
     * Obtiene todas las categorías
     * GET /api/categories
     */
    @GET
    public Response getAllCategories() {
        try {
            List<CategoryResponseDTO> categories = categoryService.getAllCategories();
            return Response.ok(categories).build();
        } catch (Exception e) {
            logger.severe("Error al obtener categorías: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene una categoría por ID
     * GET /api/categories/{id}
     */
    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") Long id) {
        try {
            Optional<CategoryResponseDTO> category = categoryService.getCategoryById(id);
            
            if (category.isEmpty()) {
                return Response.status(404)
                        .entity("Categoría no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok(category.get()).build();
        } catch (Exception e) {
            logger.severe("Error al obtener categoría por ID: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Busca una categoría por nombre
     * GET /api/categories/search?name={name}
     */
    @GET
    @Path("/search")
    public Response getCategoryByName(@QueryParam("name") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return Response.status(400)
                        .entity("El parámetro 'name' es obligatorio")
                        .build();
            }
            
            Optional<CategoryResponseDTO> category = categoryService.getCategoryByName(name);
            
            if (category.isEmpty()) {
                return Response.status(404)
                        .entity("Categoría no encontrada con nombre: " + name)
                        .build();
            }
            
            return Response.ok(category.get()).build();
        } catch (Exception e) {
            logger.severe("Error al buscar categoría por nombre: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene solo las categorías activas
     * GET /api/categories/active
     */
    @GET
    @Path("/active")
    public Response getActiveCategories() {
        try {
            List<CategoryResponseDTO> categories = categoryService.getActiveCategories();
            return Response.ok(categories).build();
        } catch (Exception e) {
            logger.severe("Error al obtener categorías activas: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Crea una nueva categoría
     * POST /api/categories
     */
    @POST
    public Response createCategory(@Valid CreateCategoryRequest request) {
        try {
            CategoryResponseDTO category = categoryService.createCategory(request);
            return Response.status(201).entity(category).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al crear categoría: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al crear categoría: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Actualiza una categoría existente
     * PUT /api/categories/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, @Valid UpdateCategoryRequest request) {
        try {
            Optional<CategoryResponseDTO> updatedCategory = categoryService.updateCategory(id, request);
            
            if (updatedCategory.isEmpty()) {
                return Response.status(404)
                        .entity("Categoría no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok(updatedCategory.get()).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al actualizar categoría: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al actualizar categoría: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Elimina una categoría
     * DELETE /api/categories/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        try {
            boolean success = categoryService.deleteCategory(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Categoría no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Categoría eliminada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al eliminar categoría: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Activa una categoría
     * PATCH /api/categories/{id}/activate
     */
    @PATCH
    @Path("/{id}/activate")
    public Response activateCategory(@PathParam("id") Long id) {
        try {
            boolean success = categoryService.activateCategory(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Categoría no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Categoría activada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al activar categoría: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Desactiva una categoría
     * PATCH /api/categories/{id}/deactivate
     */
    @PATCH
    @Path("/{id}/deactivate")
    public Response deactivateCategory(@PathParam("id") Long id) {
        try {
            boolean success = categoryService.deactivateCategory(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Categoría no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Categoría desactivada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al desactivar categoría: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
}
