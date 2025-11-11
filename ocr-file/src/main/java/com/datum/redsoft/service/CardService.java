package com.datum.redsoft.service;

import com.datum.redsoft.dto.request.CreateCardRequest;
import com.datum.redsoft.dto.request.UpdateCardRequest;
import com.datum.redsoft.dto.response.CardResponseDTO;
import com.datum.redsoft.dto.response.ExpenseGroupResponseDTO;
import com.datum.redsoft.dto.response.ExpenseResponseDTO;
import com.datum.redsoft.entity.Card;
import com.datum.redsoft.entity.Company;
import com.datum.redsoft.entity.User;
import com.datum.redsoft.entity.Invoice;
import com.datum.redsoft.enums.CardStatus;
import com.datum.redsoft.enums.CardType;
import com.datum.redsoft.enums.InvoiceStatus;
import com.datum.redsoft.repository.CardRepository;
import com.datum.redsoft.repository.CompanyRepository;
import com.datum.redsoft.repository.UserRepository;
import com.datum.redsoft.repository.InvoiceRepository;
import com.datum.redsoft.service.interfaces.ICardService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de tarjetas corporativas.
 * <p>Maneja toda la lógica de negocio relacionada con tarjetas, incluyendo:</p>
 * <ul>
 *   <li>Operaciones CRUD de tarjetas</li>
 *   <li>Gestión de estados (activa, bloqueada, suspendida, cancelada, expirada)</li>
 *   <li>Consultas por usuario, empresa, tipo, banco, etc.</li>
 *   <li>Agrupación y gestión de gastos asociados</li>
 *   <li>Aprobación de grupos de gastos mensuales</li>
 * </ul>
 * 
 * @author Datum Redsoft
 * @version 1.0
 */
@ApplicationScoped
public class CardService implements ICardService {
    
    private static final Logger logger = Logger.getLogger(CardService.class.getName());
    
    @Inject
    CardRepository cardRepository;
    
    @Inject
    UserRepository userRepository;
    
    @Inject
    CompanyRepository companyRepository;
    
    @Inject
    InvoiceRepository invoiceRepository;
    
    @Override
    public List<CardResponseDTO> getAllCards() {
        logger.info("Obteniendo todas las tarjetas");
        return cardRepository.listAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<CardResponseDTO> getCardById(Long id) {
        logger.info("Buscando tarjeta con ID: " + id);
        return cardRepository.findByIdOptional(id)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    public Optional<CardResponseDTO> getCardByMaskedNumber(String maskedCardNumber) {
        logger.info("Buscando tarjeta con número enmascarado: " + maskedCardNumber);
        return cardRepository.findByMaskedCardNumber(maskedCardNumber)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    public List<CardResponseDTO> getCardsByUser(Long userId) {
        logger.info("Obteniendo tarjetas del usuario ID: " + userId);
        return cardRepository.findByUserId(userId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CardResponseDTO> getCardsByCompany(Long companyId) {
        logger.info("Obteniendo tarjetas de la empresa ID: " + companyId);
        return cardRepository.findByCompanyId(companyId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CardResponseDTO> getActiveCards() {
        logger.info("Obteniendo tarjetas activas");
        return cardRepository.findActiveCards().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CardResponseDTO> getCardsByType(CardType cardType) {
        logger.info("Obteniendo tarjetas del tipo: " + cardType);
        return cardRepository.findByCardType(cardType).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CardResponseDTO> getCardsExpiringBefore(LocalDate expirationDate) {
        logger.info("Obteniendo tarjetas que expiran antes de: " + expirationDate);
        return cardRepository.findCardsExpiringBefore(expirationDate).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CardResponseDTO> getCardsByHolder(String holderName) {
        logger.info("Buscando tarjetas del titular: " + holderName);
        return cardRepository.findByHolderName(holderName).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CardResponseDTO> getCardsByBank(String bankName) {
        logger.info("Buscando tarjetas del banco: " + bankName);
        return cardRepository.findByIssuerBank(bankName).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CardResponseDTO createCard(CreateCardRequest request) {
        logger.info("Creando nueva tarjeta para usuario ID: " + request.getUserId());
        
        validateCardData(request);
        
        if (cardRepository.existsByCardNumber(request.getCardNumber())) {
            throw new IllegalArgumentException("Ya existe una tarjeta con el número proporcionado");
        }
        
        User user = userRepository.findByIdOptional(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.getUserId()));
        
        Company company = companyRepository.findByIdOptional(request.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con ID: " + request.getCompanyId())); 
         Card card = new Card();
        card.cardNumber = request.getCardNumber();
        card.maskedCardNumber = maskCardNumber(request.getCardNumber());
        card.holderName = request.getHolderName();
        card.cardType = request.getCardType();
        card.expirationDate = request.getExpirationDate();
        card.issuerBank = request.getIssuerBank();
        card.creditLimit = request.getCreditLimit();
        card.status = CardStatus.ACTIVE;
        card.description = request.getDescription();
        card.user = user;
        card.company = company; 
        cardRepository.persist(card);       
        cardRepository.persist(card);
        logger.info("Tarjeta creada exitosamente con ID: " + card.getId());
        
        return convertToResponseDTO(card);
    }
    
    @Override
    @Transactional
    public Optional<CardResponseDTO> updateCard(Long id, UpdateCardRequest request) {
        logger.info("Actualizando tarjeta con ID: " + id);
        
        Optional<Card> cardOpt = cardRepository.findByIdOptional(id);
        if (cardOpt.isEmpty()) {
            logger.warning("Tarjeta no encontrada con ID: " + id);
            return Optional.empty();
        }
        
        Card card = cardOpt.get();
        
        updateCardFields(card, request);
        cardRepository.persist(card);
        
        logger.info("Tarjeta actualizada exitosamente con ID: " + id);
        return Optional.of(convertToResponseDTO(card));
    }
    
    @Override
    @Transactional
    public boolean changeCardStatus(Long id, CardStatus status) {
        logger.info("Cambiando estado de tarjeta ID: " + id + " a: " + status);
        
        Optional<Card> cardOpt = cardRepository.findByIdOptional(id);
        if (cardOpt.isEmpty()) {
            logger.warning("Tarjeta no encontrada con ID: " + id);
            return false;
        }
        
        Card card = cardOpt.get();
        card.status = status;
        cardRepository.persist(card);
        
        logger.info("Estado de tarjeta actualizado exitosamente");
        return true;
    }
    
    @Override
    @Transactional
    public boolean blockCard(Long id) {
        logger.info("Bloqueando tarjeta con ID: " + id);
        return changeCardStatus(id, CardStatus.BLOCKED);
    }
    
    @Override
    @Transactional
    public boolean unblockCard(Long id) {
        logger.info("Desbloqueando tarjeta con ID: " + id);
        return changeCardStatus(id, CardStatus.ACTIVE);
    }
    
    @Override
    @Transactional
    public boolean suspendCard(Long id) {
        logger.info("Suspendiendo tarjeta con ID: " + id);
        return changeCardStatus(id, CardStatus.SUSPENDED);
    }
    
    @Override
    @Transactional
    public boolean cancelCard(Long id) {
        logger.info("Cancelando tarjeta con ID: " + id);
        return changeCardStatus(id, CardStatus.CANCELLED);
    }
    
    @Override
    @Transactional
    public boolean expireCard(Long id) {
        logger.info("Marcando tarjeta como expirada con ID: " + id);
        return changeCardStatus(id, CardStatus.EXPIRED);
    }
    

    
    /**
     * Actualiza los campos de la tarjeta solo si están presentes en el request.
     * <p>Aplica actualización parcial: solo modifica campos no nulos.</p>
     * 
     * @param card Tarjeta a actualizar
     * @param request Request con los nuevos valores (campos opcionales)
     */
    private void updateCardFields(Card card, UpdateCardRequest request) {
        if (request.getHolderName() != null && !request.getHolderName().trim().isEmpty()) {
            card.holderName = request.getHolderName().trim();
        }
        if (request.getCardType() != null) {
            card.cardType = request.getCardType();
        }
        if (request.getExpirationDate() != null) {
            card.expirationDate = request.getExpirationDate();
        }
        if (request.getIssuerBank() != null && !request.getIssuerBank().trim().isEmpty()) {
            card.issuerBank = request.getIssuerBank().trim();
        }
        if (request.getCreditLimit() != null) {
            card.creditLimit = request.getCreditLimit();
        }
        if (request.getStatus() != null) {
            card.status = request.getStatus();
        }
        if (request.getDescription() != null) {
            card.description = request.getDescription().trim();
        }
    }

    /**
     * Enmascara el número de tarjeta para proteger información sensible.
     * <p>Oculta todos los dígitos excepto los últimos 4, formateando en grupos de 4 dígitos.</p>
     * <p>Ejemplo: "1234567890123456" → "**** **** **** 3456"</p>
     * 
     * @param cardNumber Número de tarjeta completo
     * @return Número de tarjeta enmascarado y formateado
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        String masked = "*".repeat(cardNumber.length() - 4) + lastFour;
        
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < masked.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(masked.charAt(i));
        }
        
        return formatted.toString();
    }

    @Override
    public List<ExpenseGroupResponseDTO> getCardExpenses(Long cardId) {
        logger.info("Obteniendo gastos de la tarjeta con ID: " + cardId);
        
        Card card = cardRepository.findById(cardId);
        if (card == null) {
            logger.warning("No se encontró la tarjeta con ID: " + cardId);
            throw new IllegalArgumentException("No se encontró la tarjeta con ID: " + cardId);
        }
        
        List<Object[]> invoiceData = cardRepository.findInvoicesWithFieldsByCardId(cardId);
        
        if (invoiceData.isEmpty()) {
            logger.info("No se encontraron gastos para la tarjeta con ID: " + cardId);
            return new ArrayList<>();
        }
        
        List<ExpenseResponseDTO> expenses = invoiceData.stream()
                .map(this::convertToExpenseResponseDTO)
                .collect(Collectors.toList());
        
        Map<String, List<ExpenseResponseDTO>> groupedByMonth = expenses.stream()
                .collect(Collectors.groupingBy(this::getMonthYearKey));
        
        List<ExpenseGroupResponseDTO> expenseGroups = groupedByMonth.entrySet().stream()
                .map(entry -> {
                    String monthKey = entry.getKey();
                    List<ExpenseResponseDTO> monthExpenses = entry.getValue();
                    
                    BigDecimal total = monthExpenses.stream()
                            .map(ExpenseResponseDTO::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    Integer count = monthExpenses.size();
                    String groupStatus = determineGroupStatus(monthExpenses);
                    
                    return new ExpenseGroupResponseDTO(monthKey, total, count, groupStatus, monthExpenses);
                })
                .sorted((g1, g2) -> {
                    try {
                        String[] parts1 = g1.getMonth().split(" ");
                        String[] parts2 = g2.getMonth().split(" ");
                        
                        int year1 = Integer.parseInt(parts1[1]);
                        int year2 = Integer.parseInt(parts2[1]);
                        
                        if (year1 != year2) {
                            return Integer.compare(year2, year1);
                        }
                        
                        int month1 = getMonthNumber(parts1[0]);
                        int month2 = getMonthNumber(parts2[0]);
                        
                        return Integer.compare(month2, month1);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
        
        logger.info("Se encontraron " + expenseGroups.size() + " grupos de gastos para la tarjeta");
        return expenseGroups;
    }

    /**
     * Determina el estado consolidado de un grupo de gastos según los estados de sus facturas.
     * <p>Reglas de clasificación:</p>
     * <ul>
     *   <li><b>PENDIENTE:</b> Todas las facturas están en estado DRAFT (Borrador)</li>
     *   <li><b>APROBADO:</b> Todas las facturas están en estado PROCESSED (Procesada)</li>
     *   <li><b>PENDIENTE:</b> Hay mezcla de estados o estados diferentes a DRAFT/PROCESSED</li>
     * </ul>
     * 
     * @param expenses Lista de gastos del grupo a evaluar
     * @return Estado del grupo ("PENDIENTE" o "APROBADO")
     */
    private String determineGroupStatus(List<ExpenseResponseDTO> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            return "PENDIENTE";
        }
        
        Set<String> uniqueStatuses = expenses.stream()
                .map(ExpenseResponseDTO::getStatus)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        if (uniqueStatuses.size() == 1) {
            String status = uniqueStatuses.iterator().next();
            
            if (InvoiceStatus.DRAFT.getDisplayName().equals(status) || 
                InvoiceStatus.DRAFT.name().equals(status)) {
                return "PENDIENTE";
            }
            
            if (InvoiceStatus.PROCESSED.getDisplayName().equals(status) || 
                InvoiceStatus.PROCESSED.name().equals(status)) {
                return "APROBADO";
            }
        }
        
        return "PENDIENTE";
    }

    /**
     * Convierte el resultado de una consulta nativa a ExpenseResponseDTO.
     * <p>Mapea los índices del array Object[] a los campos del DTO.</p>
     * 
     * @param data Array de objetos del resultado de la consulta SQL
     * @return ExpenseResponseDTO con todos los campos mapeados
     */
    private ExpenseResponseDTO convertToExpenseResponseDTO(Object[] data) {
        return new ExpenseResponseDTO(
                (Long) data[0],                      // id (InvoiceField ID)
                (Long) data[1],                      // idInvoice (Invoice ID)
                (String) data[2],                    // vendorName
                (String) data[3],                    // concept
                (String) data[4],                    // category (nombre)
                (LocalDate) data[5],                 // invoiceDate
                (BigDecimal) data[6],                // totalAmount
                (String) data[7],                    // currency
                (Long) data[8],                      // categoryId
                (Long) data[9],                      // costCenterId
                (String) data[10],                   // costCenterName
                (String) data[11],                   // clientVisited (como String)
                (String) data[12],                   // notes
                data[13] != null ? data[13].toString() : "PENDING",  // status
                (Long) data[14],                     // countryId
                (String) data[15],                   // path
                (String) data[16]                    // fileName
        );
    }

    /**
     * Genera la clave de agrupación mes-año para un gasto.
     * 
     * @param expense Gasto del cual extraer la fecha
     * @return Clave en formato "Mes YYYY" (ej: "Diciembre 2024")
     */
    private String getMonthYearKey(ExpenseResponseDTO expense) {
        LocalDate date = expense.getInvoiceDate();
        String monthName = getMonthName(date.getMonthValue());
        return monthName + " " + date.getYear();
    }

    /**
     * Convierte número de mes a nombre en español.
     * 
     * @param monthNumber Número del mes (1-12)
     * @return Nombre del mes en español con mayúscula inicial
     */
    private String getMonthName(int monthNumber) {
        String[] months = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        return months[monthNumber - 1];
    }

    /**
     * Convierte nombre de mes en español a su número correspondiente.
     * 
     * @param monthName Nombre del mes en español (ej: "Diciembre")
     * @return Número del mes (1-12), o 1 si no se encuentra
     */
    private int getMonthNumber(String monthName) {
        Map<String, Integer> monthMap = Map.ofEntries(
                Map.entry("Enero", 1), Map.entry("Febrero", 2), Map.entry("Marzo", 3), 
                Map.entry("Abril", 4), Map.entry("Mayo", 5), Map.entry("Junio", 6),
                Map.entry("Julio", 7), Map.entry("Agosto", 8), Map.entry("Septiembre", 9), 
                Map.entry("Octubre", 10), Map.entry("Noviembre", 11), Map.entry("Diciembre", 12)
        );
        return monthMap.getOrDefault(monthName, 1);
    }
    
    /**
     * Aprueba un grupo de gastos mensuales cambiando el estado de las facturas de DRAFT a PROCESSED.
     * <p>Solo procesa facturas que cumplan todos estos criterios:</p>
     * <ul>
     *   <li>Pertenecen a la tarjeta especificada</li>
     *   <li>Están en el mes-año especificado</li>
     *   <li>Están actualmente en estado DRAFT</li>
     * </ul>
     * 
     * @param cardId ID de la tarjeta a la que pertenecen los gastos
     * @param monthYear Mes y año del grupo en formato "Mes YYYY" (ej: "Diciembre 2024")
     * @return Número de facturas actualizadas de DRAFT a PROCESSED
     * @throws IllegalArgumentException si la tarjeta no existe, el mes-año es inválido o tiene formato incorrecto
     */
    @Transactional
    public int approveExpenseGroup(Long cardId, String monthYear) {
        logger.info("Aprobando grupo de gastos - Tarjeta ID: " + cardId + ", Mes-Año: " + monthYear);
        
        Card card = cardRepository.findById(cardId);
        if (card == null) {
            logger.warning("No se encontró la tarjeta con ID: " + cardId);
            throw new IllegalArgumentException("No se encontró la tarjeta con ID: " + cardId);
        }
        
        if (monthYear == null || monthYear.trim().isEmpty()) {
            throw new IllegalArgumentException("El mes-año es obligatorio");
        }
        
        String[] parts = monthYear.trim().split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de mes-año inválido. Use formato 'Mes YYYY' (ej: 'Diciembre 2024')");
        }
        
        String monthName = parts[0];
        int year;
        try {
            year = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Año inválido en mes-año: " + parts[1]);
        }
        
        int monthNumber = getMonthNumber(monthName);
        if (monthNumber < 1 || monthNumber > 12) {
            throw new IllegalArgumentException("Nombre de mes inválido: " + monthName);
        }
        
        List<Object[]> invoiceData = cardRepository.findInvoicesWithFieldsByCardId(cardId);
        List<Long> invoiceIdsToUpdate = new ArrayList<>();
        
        for (Object[] data : invoiceData) {
            LocalDate invoiceDate = (LocalDate) data[5];
            Object statusObj = data[13];
            
            if (invoiceDate.getMonthValue() == monthNumber && invoiceDate.getYear() == year) {
                if (statusObj != null) {
                    String statusStr = statusObj.toString();
                    if (InvoiceStatus.DRAFT.getDisplayName().equals(statusStr) || 
                        InvoiceStatus.DRAFT.name().equals(statusStr)) {
                        Long invoiceId = (Long) data[1];
                        invoiceIdsToUpdate.add(invoiceId);
                    }
                }
            }
        }
        
        int updatedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        
        for (Long invoiceId : invoiceIdsToUpdate) {
            Optional<Invoice> invoiceOpt = invoiceRepository.findByIdOptional(invoiceId);
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                invoice.setStatus(InvoiceStatus.PROCESSED);
                invoice.setUpdatedAt(now);
                invoiceRepository.persist(invoice);
                updatedCount++;
                logger.info("Factura ID " + invoiceId + " actualizada de DRAFT a PROCESSED");
            }
        }
        
        logger.info("Grupo de gastos aprobado - " + updatedCount + " facturas actualizadas");
        return updatedCount;
    }
    
    /**
     * Convierte una entidad Card a su DTO de respuesta.
     * 
     * @param card Entidad Card a convertir
     * @return CardResponseDTO con todos los campos mapeados
     * @throws IllegalArgumentException si card es null
     */
    private CardResponseDTO convertToResponseDTO(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card no puede ser null");
        }
        
        return new CardResponseDTO(
                card.id,
                card.maskedCardNumber,
                card.holderName,
                card.cardType,
                card.expirationDate,
                card.issuerBank,
                card.creditLimit,
                card.status,
                card.description,
                card.user != null ? card.user.name : "Usuario no disponible",
                card.company != null ? card.company.getId() : null,
                card.company != null ? card.company.getName() : "Empresa no disponible",
                card.createdAt,
                card.updatedAt
        );
    }

    /**
     * Valida los campos obligatorios de una tarjeta al momento de creación.
     * 
     * @param request Request con los datos a validar
     * @throws IllegalArgumentException si algún campo obligatorio es inválido
     */
    private void validateCardData(CreateCardRequest request) {
        if (request.getCardNumber() == null || request.getCardNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de tarjeta es obligatorio");
        }
        
        if (request.getHolderName() == null || request.getHolderName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del titular es obligatorio");
        }
        
        if (request.getCardType() == null) {
            throw new IllegalArgumentException("El tipo de tarjeta es obligatorio");
        }
        
        if (request.getExpirationDate() == null) {
            throw new IllegalArgumentException("La fecha de expiración es obligatoria");
        }
        
        if (request.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de expiración no puede ser pasada");
        }
    }
}