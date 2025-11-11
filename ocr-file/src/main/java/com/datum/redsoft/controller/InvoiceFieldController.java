package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CreateInvoiceFieldRequest;
import com.datum.redsoft.dto.request.UpdateInvoiceFieldRequest;
import com.datum.redsoft.dto.response.InvoiceFieldResponseDTO;
import com.datum.redsoft.service.InvoiceFieldService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controller REST para gestionar InvoiceField
 * Proporciona endpoints para el CRUD completo de campos de facturas
 */
@Path("/api/invoice-fields")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceFieldController {
    
    private static final Logger logger = Logger.getLogger(InvoiceFieldController.class.getName());
    
    @Inject
    InvoiceFieldService invoiceFieldService;
    
    /**
     * GET /api/invoice-fields
     * Obtiene todos los campos de facturas
     */
    @GET
    public Response getAllInvoiceFields() {
        logger.info("GET /api/invoice-fields - Obteniendo todos los campos de facturas");
        try {
            List<InvoiceFieldResponseDTO> invoiceFields = invoiceFieldService.getAllInvoiceFields();
            return Response.ok(invoiceFields).build();
        } catch (Exception e) {
            logger.severe("Error al obtener campos de facturas: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * GET /api/invoice-fields/{id}
     * Obtiene un campo de factura por ID
     */
    @GET
    @Path("/{id}")
    public Response getInvoiceFieldById(@PathParam("id") Long id) {
        logger.info("GET /api/invoice-fields/" + id + " - Buscando campo de factura");
        
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID inválido")
                    .build();
        }
        
        try {
            Optional<InvoiceFieldResponseDTO> invoiceField = invoiceFieldService.getInvoiceFieldById(id);
            if (invoiceField.isPresent()) {
                return Response.ok(invoiceField.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Campo de factura no encontrado")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Error al obtener campo de factura por ID: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * GET /api/invoice-fields/by-invoice/{invoiceId}
     * Obtiene el campo de factura por ID de factura
     */
    @GET
    @Path("/by-invoice/{invoiceId}")
    public Response getInvoiceFieldByInvoiceId(@PathParam("invoiceId") Long invoiceId) {
        logger.info("GET /api/invoice-fields/by-invoice/" + invoiceId + " - Buscando campo por invoice ID");
        
        if (invoiceId == null || invoiceId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invoice ID inválido")
                    .build();
        }
        
        try {
            Optional<InvoiceFieldResponseDTO> invoiceField = invoiceFieldService.getInvoiceFieldByInvoiceId(invoiceId);
            if (invoiceField.isPresent()) {
                return Response.ok(invoiceField.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Campo de factura no encontrado para la factura especificada")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Error al obtener campo de factura por invoice ID: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * GET /api/invoice-fields/by-vendor?vendor={vendorName}
     * Obtiene campos de facturas por nombre de proveedor
     */
    @GET
    @Path("/by-vendor")
    public Response getInvoiceFieldsByVendor(@QueryParam("vendor") String vendorName) {
        logger.info("GET /api/invoice-fields/by-vendor?vendor=" + vendorName + " - Buscando por proveedor");
        
        if (vendorName == null || vendorName.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nombre de proveedor es requerido")
                    .build();
        }
        
        try {
            List<InvoiceFieldResponseDTO> invoiceFields = invoiceFieldService.getInvoiceFieldsByVendor(vendorName.trim());
            return Response.ok(invoiceFields).build();
        } catch (Exception e) {
            logger.severe("Error al obtener campos de facturas por proveedor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * POST /api/invoice-fields
     * Crea un nuevo campo de factura
     */
    @POST
    public Response createInvoiceField(@Valid CreateInvoiceFieldRequest request) {
        logger.info("POST /api/invoice-fields - Creando nuevo campo de factura");
        
        try {
            InvoiceFieldResponseDTO createdInvoiceField = invoiceFieldService.createInvoiceField(request);
            return Response.status(Response.Status.CREATED)
                    .entity(createdInvoiceField)
                    .build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al crear campo de factura: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al crear campo de factura: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * PUT /api/invoice-fields/{id}
     * Actualiza un campo de factura existente
     */
    @PUT
    @Path("/{id}")
    public Response updateInvoiceField(@PathParam("id") Long id, @Valid UpdateInvoiceFieldRequest request) {
        logger.info("PUT /api/invoice-fields/" + id + " - Actualizando campo de factura");
        
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID inválido")
                    .build();
        }
        
        try {
            Optional<InvoiceFieldResponseDTO> updatedInvoiceField = invoiceFieldService.updateInvoiceField(id, request);
            if (updatedInvoiceField.isPresent()) {
                return Response.ok(updatedInvoiceField.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Campo de factura no encontrado")
                        .build();
            }
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al actualizar campo de factura: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al actualizar campo de factura: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * DELETE /api/invoice-fields/{id}
     * Elimina un campo de factura
     */
    @DELETE
    @Path("/{id}")
    public Response deleteInvoiceField(@PathParam("id") Long id) {
        logger.info("DELETE /api/invoice-fields/" + id + " - Eliminando campo de factura");
        
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID inválido")
                    .build();
        }
        
        try {
            boolean deleted = invoiceFieldService.deleteInvoiceField(id);
            if (deleted) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Campo de factura no encontrado")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Error al eliminar campo de factura: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
}