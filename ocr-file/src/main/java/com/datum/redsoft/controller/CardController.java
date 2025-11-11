package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CreateCardRequest;
import com.datum.redsoft.dto.request.UpdateCardRequest;
import com.datum.redsoft.dto.response.CardResponseDTO;
import com.datum.redsoft.dto.response.ExpenseGroupResponseDTO;
import com.datum.redsoft.enums.CardStatus;
import com.datum.redsoft.enums.CardType;
import com.datum.redsoft.service.interfaces.ICardService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controlador REST para la gestión de tarjetas
 * Proporciona endpoints para todas las operaciones CRUD de tarjetas
 */
@Path("/api/cards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CardController {
    
    private static final Logger logger = Logger.getLogger(CardController.class.getName());
    
    @Inject
    ICardService cardService;
    
    /**
     * Obtiene todas las tarjetas
     * GET /api/cards
     */
    @GET
    public Response getAllCards() {
        try {
            List<CardResponseDTO> cards = cardService.getAllCards();
            return Response.ok(cards).build();
        } catch (Exception e) {
            logger.severe("Error al obtener tarjetas: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene una tarjeta por ID
     * GET /api/cards/{id}
     */
    @GET
    @Path("/{id}")
    public Response getCardById(@PathParam("id") Long id) {
        try {
            Optional<CardResponseDTO> card = cardService.getCardById(id);
            
            if (card.isEmpty()) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok(card.get()).build();
        } catch (Exception e) {
            logger.severe("Error al obtener tarjeta por ID: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene los gastos de una tarjeta agrupados por mes-año
     * GET /api/cards/{id}/expenses
     */
    @GET
    @Path("/{id}/expenses")
    public Response getCardExpenses(@PathParam("id") Long cardId) {
        try {
            List<ExpenseGroupResponseDTO> expenses = cardService.getCardExpenses(cardId);
            return Response.ok(expenses).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al obtener gastos de tarjeta: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al obtener gastos de tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene una tarjeta por número enmascarado
     * GET /api/cards/masked/{maskedNumber}
     */
    @GET
    @Path("/masked/{maskedNumber}")
    public Response getCardByMaskedNumber(@PathParam("maskedNumber") String maskedNumber) {
        try {
            Optional<CardResponseDTO> card = cardService.getCardByMaskedNumber(maskedNumber);
            
            if (card.isEmpty()) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con número: " + maskedNumber)
                        .build();
            }
            
            return Response.ok(card.get()).build();
        } catch (Exception e) {
            logger.severe("Error al obtener tarjeta por número enmascarado: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene tarjetas por usuario
     * GET /api/cards/user/{userId}
     */
    @GET
    @Path("/user/{userId}")
    public Response getCardsByUser(@PathParam("userId") Long userId) {
        try {
            List<CardResponseDTO> cards = cardService.getCardsByUser(userId);
            return Response.ok(cards).build();
        } catch (Exception e) {
            logger.severe("Error al obtener tarjetas por usuario: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene tarjetas por empresa
     * GET /api/cards/company/{companyId}
     */
    @GET
    @Path("/company/{companyId}")
    public Response getCardsByCompany(@PathParam("companyId") Long companyId) {
        try {
            List<CardResponseDTO> cards = cardService.getCardsByCompany(companyId);
            return Response.ok(cards).build();
        } catch (Exception e) {
            logger.severe("Error al obtener tarjetas por empresa: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene solo tarjetas activas
     * GET /api/cards/active
     */
    @GET
    @Path("/active")
    public Response getActiveCards() {
        try {
            List<CardResponseDTO> cards = cardService.getActiveCards();
            return Response.ok(cards).build();
        } catch (Exception e) {
            logger.severe("Error al obtener tarjetas activas: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene tarjetas por tipo
     * GET /api/cards/type/{cardType}
     */
    @GET
    @Path("/type/{cardType}")
    public Response getCardsByType(@PathParam("cardType") CardType cardType) {
        try {
            List<CardResponseDTO> cards = cardService.getCardsByType(cardType);
            return Response.ok(cards).build();
        } catch (Exception e) {
            logger.severe("Error al obtener tarjetas por tipo: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Obtiene tarjetas que expiran antes de una fecha
     * GET /api/cards/expiring-before/{date}
     */
    @GET
    @Path("/expiring-before/{date}")
    public Response getCardsExpiringBefore(@PathParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<CardResponseDTO> cards = cardService.getCardsExpiringBefore(date);
            return Response.ok(cards).build();
        } catch (Exception e) {
            logger.severe("Error al obtener tarjetas que expiran: " + e.getMessage());
            return Response.status(400)
                    .entity("Formato de fecha inválido. Use formato YYYY-MM-DD")
                    .build();
        }
    }
    
    /**
     * Busca tarjetas por titular
     * GET /api/cards/search/holder?name={name}
     */
    @GET
    @Path("/search/holder")
    public Response getCardsByHolder(@QueryParam("name") String holderName) {
        try {
            if (holderName == null || holderName.trim().isEmpty()) {
                return Response.status(400)
                        .entity("El parámetro 'name' es obligatorio")
                        .build();
            }
            
            List<CardResponseDTO> cards = cardService.getCardsByHolder(holderName);
            return Response.ok(cards).build();
        } catch (Exception e) {
            logger.severe("Error al buscar tarjetas por titular: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Busca tarjetas por banco
     * GET /api/cards/search/bank?name={name}
     */
    @GET
    @Path("/search/bank")
    public Response getCardsByBank(@QueryParam("name") String bankName) {
        try {
            if (bankName == null || bankName.trim().isEmpty()) {
                return Response.status(400)
                        .entity("El parámetro 'name' es obligatorio")
                        .build();
            }
            
            List<CardResponseDTO> cards = cardService.getCardsByBank(bankName);
            return Response.ok(cards).build();
        } catch (Exception e) {
            logger.severe("Error al buscar tarjetas por banco: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Crea una nueva tarjeta
     * POST /api/cards
     */
    @POST
    public Response createCard(@Valid CreateCardRequest request) {
        try {
            CardResponseDTO card = cardService.createCard(request);
            return Response.status(201).entity(card).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al crear tarjeta: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al crear tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Actualiza una tarjeta existente
     * PUT /api/cards/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateCard(@PathParam("id") Long id, @Valid UpdateCardRequest request) {
        try {
            Optional<CardResponseDTO> updatedCard = cardService.updateCard(id, request);
            
            if (updatedCard.isEmpty()) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok(updatedCard.get()).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al actualizar tarjeta: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al actualizar tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Cambia el estado de una tarjeta
     * PATCH /api/cards/{id}/status/{status}
     */
    @PATCH
    @Path("/{id}/status/{status}")
    public Response changeCardStatus(@PathParam("id") Long id, @PathParam("status") CardStatus status) {
        try {
            boolean success = cardService.changeCardStatus(id, status);
            
            if (!success) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Estado de la tarjeta actualizado correctamente a: " + status.getDisplayName())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al cambiar estado de tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Bloquea una tarjeta
     * PATCH /api/cards/{id}/block
     */
    @PATCH
    @Path("/{id}/block")
    public Response blockCard(@PathParam("id") Long id) {
        try {
            boolean success = cardService.blockCard(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Tarjeta bloqueada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al bloquear tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Desbloquea una tarjeta
     * PATCH /api/cards/{id}/unblock
     */
    @PATCH
    @Path("/{id}/unblock")
    public Response unblockCard(@PathParam("id") Long id) {
        try {
            boolean success = cardService.unblockCard(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Tarjeta desbloqueada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al desbloquear tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Suspende una tarjeta
     * PATCH /api/cards/{id}/suspend
     */
    @PATCH
    @Path("/{id}/suspend")
    public Response suspendCard(@PathParam("id") Long id) {
        try {
            boolean success = cardService.suspendCard(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Tarjeta suspendida correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al suspender tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Cancela una tarjeta
     * PATCH /api/cards/{id}/cancel
     */
    @PATCH
    @Path("/{id}/cancel")
    public Response cancelCard(@PathParam("id") Long id) {
        try {
            boolean success = cardService.cancelCard(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Tarjeta cancelada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al cancelar tarjeta: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Marca una tarjeta como expirada
     * PATCH /api/cards/{id}/expire
     */
    @PATCH
    @Path("/{id}/expire")
    public Response expireCard(@PathParam("id") Long id) {
        try {
            boolean success = cardService.expireCard(id);
            
            if (!success) {
                return Response.status(404)
                        .entity("Tarjeta no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok()
                    .entity("Tarjeta marcada como expirada correctamente")
                    .build();
        } catch (Exception e) {
            logger.severe("Error al marcar tarjeta como expirada: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
    /**
     * Aprueba un grupo de gastos de una tarjeta
     * <p>Cambia el estado de todas las facturas de DRAFT a PROCESSED para el mes-año especificado.
     * Solo se procesan las facturas que estén actualmente en estado DRAFT.</p>
     * 
     * @param cardId ID de la tarjeta
     * @param monthYear Mes y año del grupo en formato "Mes YYYY" (ej: "Diciembre 2024")
     * @return Response con el número de facturas actualizadas
     * 
     * @apiNote PATCH /api/cards/{id}/expenses/approve?monthYear={monthYear}
     */
    @PATCH
    @Path("/{id}/expenses/approve")
    public Response approveExpenseGroup(
            @PathParam("id") Long cardId,
            @QueryParam("monthYear") String monthYear) {
        try {
            if (monthYear == null || monthYear.trim().isEmpty()) {
                return Response.status(400)
                        .entity("El parámetro 'monthYear' es obligatorio (formato: 'Mes YYYY', ej: 'Diciembre 2024')")
                        .build();
            }
            
            int updatedCount = cardService.approveExpenseGroup(cardId, monthYear);
            
            return Response.ok()
                    .entity(String.format("Grupo de gastos aprobado correctamente. %d factura(s) actualizada(s) de DRAFT a PROCESSED", updatedCount))
                    .build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al aprobar grupo de gastos: " + e.getMessage());
            return Response.status(400)
                    .entity("Error de validación: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.severe("Error al aprobar grupo de gastos: " + e.getMessage());
            return Response.status(500)
                    .entity("Error interno del servidor")
                    .build();
        }
    }
    
}

