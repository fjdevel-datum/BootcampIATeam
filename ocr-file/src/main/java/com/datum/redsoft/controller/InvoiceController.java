package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CreateInvoiceRequest;
import com.datum.redsoft.dto.request.UpdateInvoiceRequest;
import com.datum.redsoft.dto.request.CreateCompleteInvoiceRequest;
import com.datum.redsoft.dto.request.UpdateCompleteInvoiceRequest;
import com.datum.redsoft.dto.response.InvoiceResponseDTO;
import com.datum.redsoft.dto.response.CompleteInvoiceResponseDTO;
import com.datum.redsoft.enums.InvoiceStatus;
import com.datum.redsoft.service.interfaces.IInvoiceService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controlador REST para la gestión de facturas
 * Proporciona endpoints para todas las operaciones CRUD de facturas
 */
@Path("/api/invoices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvoiceController {
    
    private static final Logger logger = Logger.getLogger(InvoiceController.class.getName());
    
    @Inject
    IInvoiceService invoiceService;
    
    /**
     * Obtiene todas las facturas
     * GET /api/invoices
     */
    @GET
    public Response getAllInvoices() {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices();
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene una factura por ID
     * GET /api/invoices/{id}
     */
    @GET
    @Path("/{id}")
    public Response getInvoiceById(@PathParam("id") Long id) {
        try {
            Optional<InvoiceResponseDTO> invoice = invoiceService.getInvoiceById(id);
            
            if (invoice.isEmpty()) {
                return Response.status(404)
                        .entity("Factura no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok(invoice.get()).build();
        } catch (Exception e) {
            logger.severe("Error al obtener factura por ID: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas por usuario
     * GET /api/invoices/user/{userId}
     */
    @GET
    @Path("/user/{userId}")
    public Response getInvoicesByUser(@PathParam("userId") Long userId) {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByUser(userId);
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas por usuario: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas por empresa
     * GET /api/invoices/company/{companyId}
     */
    @GET
    @Path("/company/{companyId}")
    public Response getInvoicesByCompany(@PathParam("companyId") Long companyId) {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByCompany(companyId);
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas por empresa: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas por tarjeta
     * GET /api/invoices/card/{cardId}
     */
    @GET
    @Path("/card/{cardId}")
    public Response getInvoicesByCard(@PathParam("cardId") Long cardId) {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByCard(cardId);
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas por tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas por país
     * GET /api/invoices/country/{countryId}
     */
    @GET
    @Path("/country/{countryId}")
    public Response getInvoicesByCountry(@PathParam("countryId") Long countryId) {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByCountry(countryId);
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas por país: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas por estado
     * GET /api/invoices/status/{status}
     */
    @GET
    @Path("/status/{status}")
    public Response getInvoicesByStatus(@PathParam("status") InvoiceStatus status) {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByStatus(status);
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas por estado: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas en borrador
     * GET /api/invoices/draft
     */
    @GET
    @Path("/draft")
    public Response getDraftInvoices() {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getDraftInvoices();
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas en borrador: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas pendientes
     * GET /api/invoices/pending
     */
    @GET
    @Path("/pending")
    public Response getPendingInvoices() {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getPendingInvoices();
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas pendientes: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas procesadas
     * GET /api/invoices/processed
     */
    @GET
    @Path("/processed")
    public Response getProcessedInvoices() {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getProcessedInvoices();
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas procesadas: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene facturas aprobadas
     * GET /api/invoices/approved
     */
    @GET
    @Path("/approved")
    public Response getApprovedInvoices() {
        try {
            List<InvoiceResponseDTO> invoices = invoiceService.getApprovedInvoices();
            return Response.ok(invoices).build();
        } catch (Exception e) {
            logger.severe("Error al obtener facturas aprobadas: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Crea una nueva factura
     * POST /api/invoices
     */
    @POST
    public Response createInvoice(@Valid CreateInvoiceRequest request) {
        try {
            InvoiceResponseDTO invoice = invoiceService.createInvoice(request);
            return Response.status(201).entity(invoice).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al crear factura: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al crear factura: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Crea una factura completa con campos en una sola transacción
     * POST /api/invoices/complete
     */
    @POST
    @Path("/complete")
    public Response createCompleteInvoice(@Valid CreateCompleteInvoiceRequest request) {
        try {
            CompleteInvoiceResponseDTO completeInvoice = invoiceService.createCompleteInvoice(request);
            return Response.status(201).entity(completeInvoice).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al crear factura completa: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al crear factura completa: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor al procesar la transacción")
                    .build();
        }
    }
    
    /**
     * Actualiza una factura completa con campos en una sola transacción
     * No actualiza path, fileName, cardId ni status
     * PUT /api/invoices/complete
     */
    @PUT
    @Path("/complete")
    public Response updateCompleteInvoice(@Valid UpdateCompleteInvoiceRequest request) {
        try {
            Optional<CompleteInvoiceResponseDTO> updatedInvoice = invoiceService.updateCompleteInvoice(request);
            
            if (updatedInvoice.isEmpty()) {
                return Response.status(404)
                        .entity("Invoice o InvoiceField no encontrado con los IDs proporcionados")
                        .build();
            }
            
            return Response.ok(updatedInvoice.get()).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al actualizar factura completa: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al actualizar factura completa: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor al procesar la transacción")
                    .build();
        }
    }
    
    /**
     * Actualiza una factura existente
     * PUT /api/invoices/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateInvoice(@PathParam("id") Long id, @Valid UpdateInvoiceRequest request) {
        try {
            Optional<InvoiceResponseDTO> updatedInvoice = invoiceService.updateInvoice(id, request);
            
            if (updatedInvoice.isEmpty()) {
                return Response.status(404)
                        .entity("Factura no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok(updatedInvoice.get()).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al actualizar factura: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al actualizar factura: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Elimina una factura
     * DELETE /api/invoices/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteInvoice(@PathParam("id") Long id) {
        try {
            boolean success = invoiceService.deleteInvoice(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Factura no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Factura eliminada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al eliminar factura: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Procesa una factura
     * PATCH /api/invoices/{id}/process
     */
    @PATCH
    @Path("/{id}/process")
    public Response processInvoice(@PathParam("id") Long id) {
        try {
            boolean success = invoiceService.processInvoice(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Factura no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Factura procesada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al procesar factura: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Aprueba una factura
     * PATCH /api/invoices/{id}/approve
     */
    @PATCH
    @Path("/{id}/approve")
    public Response approveInvoice(@PathParam("id") Long id) {
        try {
            boolean success = invoiceService.approveInvoice(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Factura no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Factura aprobada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al aprobar factura: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Rechaza una factura
     * PATCH /api/invoices/{id}/reject
     */
    @PATCH
    @Path("/{id}/reject")
    public Response rejectInvoice(@PathParam("id") Long id) {
        try {
            boolean success = invoiceService.rejectInvoice(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Factura no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Factura rechazada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al rechazar factura: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Marca una factura como pagada
     * PATCH /api/invoices/{id}/paid
     */
    @PATCH
    @Path("/{id}/paid")
    public Response markAsPaid(@PathParam("id") Long id) {
        try {
            boolean success = invoiceService.markAsPaid(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Factura no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Factura marcada como pagada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al marcar factura como pagada: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Cancela una factura
     * PATCH /api/invoices/{id}/cancel
     */
    @PATCH
    @Path("/{id}/cancel")
    public Response cancelInvoice(@PathParam("id") Long id) {
        try {
            boolean success = invoiceService.cancelInvoice(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Factura no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Factura cancelada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al cancelar factura: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
}
