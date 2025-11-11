package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.request.CreateCardRequest;
import com.datum.redsoft.dto.request.UpdateCardRequest;
import com.datum.redsoft.dto.response.CardResponseDTO;
import com.datum.redsoft.enums.CardStatus;
import com.datum.redsoft.enums.CardType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones disponibles para el servicio de tarjetas
 * Proporciona métodos para gestionar el ciclo de vida completo de las tarjetas
 */
public interface ICardService {
    
    /**
     * Obtiene todas las tarjetas del sistema
     * @return Lista de todas las tarjetas
     */
    List<CardResponseDTO> getAllCards();
    
    /**
     * Obtiene una tarjeta por su ID
     * @param id ID de la tarjeta
     * @return Optional con la tarjeta encontrada o vacío si no existe
     */
    Optional<CardResponseDTO> getCardById(Long id);
    
    /**
     * Obtiene una tarjeta por su número (enmascarado)
     * @param maskedCardNumber Número de tarjeta enmascarado
     * @return Optional con la tarjeta encontrada o vacío si no existe
     */
    Optional<CardResponseDTO> getCardByMaskedNumber(String maskedCardNumber);
    
    /**
     * Obtiene todas las tarjetas de un usuario específico
     * @param userId ID del usuario
     * @return Lista de tarjetas del usuario
     */
    List<CardResponseDTO> getCardsByUser(Long userId);
    
    /**
     * Obtiene todas las tarjetas de una empresa específica
     * @param companyId ID de la empresa
     * @return Lista de tarjetas de la empresa
     */
    List<CardResponseDTO> getCardsByCompany(Long companyId);
    
    /**
     * Obtiene todas las tarjetas activas
     * @return Lista de tarjetas activas
     */
    List<CardResponseDTO> getActiveCards();
    
    /**
     * Obtiene todas las tarjetas de un tipo específico
     * @param cardType Tipo de tarjeta
     * @return Lista de tarjetas del tipo especificado
     */
    List<CardResponseDTO> getCardsByType(CardType cardType);
    
    /**
     * Obtiene todas las tarjetas que expiran antes de una fecha específica
     * @param expirationDate Fecha límite de expiración
     * @return Lista de tarjetas que expiran antes de la fecha
     */
    List<CardResponseDTO> getCardsExpiringBefore(LocalDate expirationDate);
    
    /**
     * Obtiene tarjetas por titular
     * @param holderName Nombre del titular
     * @return Lista de tarjetas del titular
     */
    List<CardResponseDTO> getCardsByHolder(String holderName);
    
    /**
     * Busca tarjetas por banco emisor
     * @param bankName Nombre del banco
     * @return Lista de tarjetas del banco
     */
    List<CardResponseDTO> getCardsByBank(String bankName);
    
    /**
     * Crea una nueva tarjeta
     * @param request Datos de la tarjeta a crear
     * @return Tarjeta creada
     * @throws IllegalArgumentException si los datos son inválidos
     */
    CardResponseDTO createCard(CreateCardRequest request);
    
    /**
     * Actualiza una tarjeta existente
     * @param id ID de la tarjeta a actualizar
     * @param request Datos actualizados
     * @return Optional con la tarjeta actualizada o vacío si no existe
     * @throws IllegalArgumentException si los datos son inválidos
     */
    Optional<CardResponseDTO> updateCard(Long id, UpdateCardRequest request);
    
    /**
     * Cambia el estado de una tarjeta
     * @param id ID de la tarjeta
     * @param status Nuevo estado
     * @return true si se actualizó correctamente, false si no se encontró
     */
    boolean changeCardStatus(Long id, CardStatus status);
    
    /**
     * Bloquea una tarjeta
     * @param id ID de la tarjeta
     * @return true si se bloqueó correctamente, false si no se encontró
     */
    boolean blockCard(Long id);
    
    /**
     * Desbloquea una tarjeta
     * @param id ID de la tarjeta
     * @return true si se desbloqueó correctamente, false si no se encontró
     */
    boolean unblockCard(Long id);
    
    /**
     * Suspende una tarjeta
     * @param id ID de la tarjeta
     * @return true si se suspendió correctamente, false si no se encontró
     */
    boolean suspendCard(Long id);
    
    /**
     * Cancela una tarjeta
     * @param id ID de la tarjeta
     * @return true si se canceló correctamente, false si no se encontró
     */
    boolean cancelCard(Long id);
    
    /**
     * Marca una tarjeta como expirada
     * @param id ID de la tarjeta
     * @return true si se marcó como expirada, false si no se encontró
     */
    boolean expireCard(Long id);
    
    /**
     * Obtiene los gastos de una tarjeta agrupados por mes-año
     * @param cardId ID de la tarjeta
     * @return Lista de gastos agrupados por mes con totales calculados
     */
    List<com.datum.redsoft.dto.response.ExpenseGroupResponseDTO> getCardExpenses(Long cardId);
    
    /**
     * Aprueba un grupo de gastos cambiando el estado de todas las facturas de DRAFT a PROCESSED
     * @param cardId ID de la tarjeta
     * @param monthYear Mes-año del grupo (ej: "Diciembre 2024")
     * @return Número de facturas actualizadas
     * @throws IllegalArgumentException si la tarjeta no existe o el formato es inválido
     */
    int approveExpenseGroup(Long cardId, String monthYear);

}