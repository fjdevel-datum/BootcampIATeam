package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CreateCostCenterRequest;
import com.datum.redsoft.dto.request.UpdateCostCenterRequest;
import com.datum.redsoft.dto.response.CostCenterResponseDTO;
import com.datum.redsoft.service.interfaces.ICostCenterService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controlador REST para la gestión de centros de costo
 * Proporciona endpoints para todas las operaciones CRUD de centros de costo
 */
@Path("/api/cost-centers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CostCenterController {
    
    private static final Logger logger = Logger.getLogger(CostCenterController.class.getName());
    
    @Inject
    ICostCenterService costCenterService;
    
    /**
     * Obtiene todos los centros de costo
     * GET /api/cost-centers
     */
    @GET
    public Response getAllCostCenters() {
        try {
            List<CostCenterResponseDTO> costCenters = costCenterService.getAllCostCenters();
            return Response.ok(costCenters).build();
        } catch (Exception e) {
            logger.severe("Error al obtener centros de costo: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene un centro de costo por ID
     * GET /api/cost-centers/{id}
     */
    @GET
    @Path("/{id}")
    public Response getCostCenterById(@PathParam("id") Long id) {
        try {
            Optional<CostCenterResponseDTO> costCenter = costCenterService.getCostCenterById(id);
            
            if (costCenter.isEmpty()) {
                return Response.status(404)
                        .entity("Centro de costo no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok(costCenter.get()).build();
        } catch (Exception e) {
            logger.severe("Error al obtener centro de costo por ID: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Busca un centro de costo por código
     * GET /api/cost-centers/code/{code}
     */
    @GET
    @Path("/code/{code}")
    public Response getCostCenterByCode(@PathParam("code") String code) {
        try {
            Optional<CostCenterResponseDTO> costCenter = costCenterService.getCostCenterByCode(code);
            
            if (costCenter.isEmpty()) {
                return Response.status(404)
                        .entity("Centro de costo no encontrado con código: " + code)
                        .build();
            }
            
            return Response.ok(costCenter.get()).build();
        } catch (Exception e) {
            logger.severe("Error al buscar centro de costo por código: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene solo los centros de costo activos
     * GET /api/cost-centers/active
     */
    @GET
    @Path("/active")
    public Response getActiveCostCenters() {
        try {
            List<CostCenterResponseDTO> costCenters = costCenterService.getActiveCostCenters();
            return Response.ok(costCenters).build();
        } catch (Exception e) {
            logger.severe("Error al obtener centros de costo activos: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Busca centros de costo por nombre
     * GET /api/cost-centers/search?name={name}
     */
    @GET
    @Path("/search")
    public Response searchByName(@QueryParam("name") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return Response.status(400)
                        .entity("El parámetro 'name' es obligatorio")
                        .build();
            }
            
            List<CostCenterResponseDTO> costCenters = costCenterService.searchByName(name);
            return Response.ok(costCenters).build();
        } catch (Exception e) {
            logger.severe("Error al buscar centros de costo por nombre: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Crea un nuevo centro de costo
     * POST /api/cost-centers
     */
    @POST
    public Response createCostCenter(@Valid CreateCostCenterRequest request) {
        try {
            CostCenterResponseDTO costCenter = costCenterService.createCostCenter(request);
            return Response.status(201).entity(costCenter).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al crear centro de costo: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al crear centro de costo: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Actualiza un centro de costo existente
     * PUT /api/cost-centers/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateCostCenter(@PathParam("id") Long id, @Valid UpdateCostCenterRequest request) {
        try {
            Optional<CostCenterResponseDTO> updatedCostCenter = costCenterService.updateCostCenter(id, request);
            
            if (updatedCostCenter.isEmpty()) {
                return Response.status(404)
                        .entity("Centro de costo no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok(updatedCostCenter.get()).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al actualizar centro de costo: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al actualizar centro de costo: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Elimina un centro de costo
     * DELETE /api/cost-centers/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCostCenter(@PathParam("id") Long id) {
        try {
            boolean success = costCenterService.deleteCostCenter(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Centro de costo no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Centro de costo eliminado correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al eliminar centro de costo: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Activa un centro de costo
     * PATCH /api/cost-centers/{id}/activate
     */
    @PATCH
    @Path("/{id}/activate")
    public Response activateCostCenter(@PathParam("id") Long id) {
        try {
            boolean success = costCenterService.activateCostCenter(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Centro de costo no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Centro de costo activado correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al activar centro de costo: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Desactiva un centro de costo
     * PATCH /api/cost-centers/{id}/deactivate
     */
    @PATCH
    @Path("/{id}/deactivate")
    public Response deactivateCostCenter(@PathParam("id") Long id) {
        try {
            boolean success = costCenterService.deactivateCostCenter(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Centro de costo no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Centro de costo desactivado correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al desactivar centro de costo: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
}
