package com.datum.redsoft.service;

import com.datum.redsoft.dto.request.CreateInvoiceRequest;
import com.datum.redsoft.dto.request.UpdateInvoiceRequest;
import com.datum.redsoft.dto.request.CreateCompleteInvoiceRequest;
import com.datum.redsoft.dto.request.UpdateCompleteInvoiceRequest;
import com.datum.redsoft.dto.response.InvoiceResponseDTO;
import com.datum.redsoft.dto.response.CompleteInvoiceResponseDTO;
import com.datum.redsoft.entity.*;
import com.datum.redsoft.repository.InvoiceFieldRepository;
import com.datum.redsoft.repository.CategoryRepository;
import com.datum.redsoft.repository.CostCenterRepository;
import com.datum.redsoft.enums.InvoiceStatus;
import com.datum.redsoft.repository.*;
import com.datum.redsoft.service.interfaces.IInvoiceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de facturas
 * Maneja toda la lógica de negocio relacionada con las facturas
 */
@ApplicationScoped
public class InvoiceService implements IInvoiceService {
    
    private static final Logger logger = Logger.getLogger(InvoiceService.class.getName());
    
    @Inject
    InvoiceRepository invoiceRepository;
    
    @Inject
    UserRepository userRepository;
    
    @Inject
    CompanyRepository companyRepository;
    
    @Inject
    CardRepository cardRepository;
    
    @Inject
    CountryRepository countryRepository;
    
    @Inject
    InvoiceFieldRepository invoiceFieldRepository;
    
    @Inject
    CategoryRepository categoryRepository;
    
    @Inject
    CostCenterRepository costCenterRepository;
    
    @Override
    public List<InvoiceResponseDTO> getAllInvoices() {
        logger.info("Obteniendo todas las facturas");
        return invoiceRepository.listAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<InvoiceResponseDTO> getInvoiceById(Long id) {
        logger.info("Buscando factura con ID: " + id);
        return invoiceRepository.findByIdOptional(id)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    public List<InvoiceResponseDTO> getInvoicesByUser(Long userId) {
        logger.info("Obteniendo facturas del usuario ID: " + userId);
        return invoiceRepository.findByUserId(userId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvoiceResponseDTO> getInvoicesByCompany(Long companyId) {
        logger.info("Obteniendo facturas de la empresa ID: " + companyId);
        return invoiceRepository.findByCompanyId(companyId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvoiceResponseDTO> getInvoicesByCard(Long cardId) {
        logger.info("Obteniendo facturas de la tarjeta ID: " + cardId);
        return invoiceRepository.findByCardId(cardId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvoiceResponseDTO> getInvoicesByCountry(Long countryId) {
        logger.info("Obteniendo facturas del país ID: " + countryId);
        return invoiceRepository.findByCountryId(countryId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvoiceResponseDTO> getInvoicesByStatus(InvoiceStatus status) {
        logger.info("Obteniendo facturas con estado: " + status);
        return invoiceRepository.findByStatus(status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvoiceResponseDTO> getDraftInvoices() {
        logger.info("Obteniendo facturas en borrador");
        return invoiceRepository.findDraftInvoices().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvoiceResponseDTO> getPendingInvoices() {
        logger.info("Obteniendo facturas pendientes");
        return invoiceRepository.findPendingInvoices().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvoiceResponseDTO> getProcessedInvoices() {
        logger.info("Obteniendo facturas procesadas");
        return invoiceRepository.findProcessedInvoices().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvoiceResponseDTO> getApprovedInvoices() {
        logger.info("Obteniendo facturas aprobadas");
        return invoiceRepository.findApprovedInvoices().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public InvoiceResponseDTO createInvoice(CreateInvoiceRequest request) {
        logger.info("Creando nueva factura para usuario ID: " + request.getUserId());
        
        // Validar que exista el usuario
        User user = userRepository.findByIdOptional(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.getUserId()));
        
        // Validar que exista la empresa
        Company company = companyRepository.findByIdOptional(request.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con ID: " + request.getCompanyId()));
        
        // Validar que exista el país
        Country country = countryRepository.findByIdOptional(request.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("País no encontrado con ID: " + request.getCountryId()));
        
        // Validar que exista la tarjeta (si se proporciona)
        Card card = null;
        if (request.getCardId() != null) {
            card = cardRepository.findByIdOptional(request.getCardId())
                    .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada con ID: " + request.getCardId()));
        }
        
        // Crear la factura
        Invoice invoice = new Invoice();
        invoice.setUser(user);
        invoice.setCompany(company);
        invoice.setCountry(country);
        invoice.setCard(card);
        invoice.setPath(request.getPath());
        invoice.setFileName(request.getFileName());
        invoice.setStatus(InvoiceStatus.DRAFT);
        
        invoiceRepository.persist(invoice);
        logger.info("Factura creada exitosamente con ID: " + invoice.getId());
        
        return convertToResponseDTO(invoice);
    }
    
    @Override
    @Transactional
    public Optional<InvoiceResponseDTO> updateInvoice(Long id, UpdateInvoiceRequest request) {
        logger.info("Actualizando factura con ID: " + id);
        
        Optional<Invoice> invoiceOpt = invoiceRepository.findByIdOptional(id);
        if (invoiceOpt.isEmpty()) {
            logger.warning("Factura no encontrada con ID: " + id);
            return Optional.empty();
        }
        
        Invoice invoice = invoiceOpt.get();
        
        // Actualizar campos no nulos
        if (request.getCardId() != null) {
            Card card = cardRepository.findByIdOptional(request.getCardId())
                    .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada con ID: " + request.getCardId()));
            invoice.setCard(card);
        }
        
        if (request.getCountryId() != null) {
            Country country = countryRepository.findByIdOptional(request.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("País no encontrado con ID: " + request.getCountryId()));
            invoice.setCountry(country);
        }
        
        if (request.getPath() != null) {
            invoice.setPath(request.getPath());
        }
        
        if (request.getFileName() != null) {
            invoice.setFileName(request.getFileName());
        }
        
        if (request.getStatus() != null) {
            invoice.setStatus(request.getStatus());
        }
        
        invoice.setUpdatedAt(LocalDateTime.now());
        invoiceRepository.merge(invoice);
        
        logger.info("Factura actualizada exitosamente con ID: " + id);
        return Optional.of(convertToResponseDTO(invoice));
    }
    
    @Override
    @Transactional
    public boolean deleteInvoice(Long id) {
        logger.info("Eliminando factura con ID: " + id);
        
        Optional<Invoice> invoiceOpt = invoiceRepository.findByIdOptional(id);
        if (invoiceOpt.isEmpty()) {
            logger.warning("Factura no encontrada con ID: " + id);
            return false;
        }
        
        invoiceRepository.remove(invoiceOpt.get());
        logger.info("Factura eliminada exitosamente");
        return true;
    }
    
    @Override
    @Transactional
    public boolean changeInvoiceStatus(Long id, InvoiceStatus status) {
        logger.info("Cambiando estado de factura ID: " + id + " a: " + status);
        
        Optional<Invoice> invoiceOpt = invoiceRepository.findByIdOptional(id);
        if (invoiceOpt.isEmpty()) {
            logger.warning("Factura no encontrada con ID: " + id);
            return false;
        }
        
        Invoice invoice = invoiceOpt.get();
        invoice.setStatus(status);
        invoice.setUpdatedAt(LocalDateTime.now());
        invoiceRepository.merge(invoice);
        
        logger.info("Estado de factura actualizado exitosamente");
        return true;
    }
    
    @Override
    @Transactional
    public boolean processInvoice(Long id) {
        logger.info("Procesando factura con ID: " + id);
        return changeInvoiceStatus(id, InvoiceStatus.PROCESSING);
    }
    
    @Override
    @Transactional
    public boolean approveInvoice(Long id) {
        logger.info("Aprobando factura con ID: " + id);
        return changeInvoiceStatus(id, InvoiceStatus.APPROVED);
    }
    
    @Override
    @Transactional
    public boolean rejectInvoice(Long id) {
        logger.info("Rechazando factura con ID: " + id);
        return changeInvoiceStatus(id, InvoiceStatus.REJECTED);
    }
    
    @Override
    @Transactional
    public boolean markAsPaid(Long id) {
        logger.info("Marcando factura como pagada con ID: " + id);
        return changeInvoiceStatus(id, InvoiceStatus.PAID);
    }
    
    @Override
    @Transactional
    public boolean cancelInvoice(Long id) {
        logger.info("Cancelando factura con ID: " + id);
        return changeInvoiceStatus(id, InvoiceStatus.CANCELLED);
    }
    
    @Override
    /**
     * Crea un Invoice completo con InvoiceField en una sola transacción
     * Si algo falla, toda la operación se revierte automáticamente
     */
    @Transactional
    public CompleteInvoiceResponseDTO createCompleteInvoice(CreateCompleteInvoiceRequest request) {
        logger.info("Creando factura completa para usuario ID: " + request.getUserId());
        
        try {
            // Validar que exista el usuario
            User user = userRepository.findByIdOptional(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.getUserId()));
            
            // Validar que exista la empresa
            Company company = companyRepository.findByIdOptional(request.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con ID: " + request.getCompanyId()));
            
            // Validar que exista el país
            Country country = countryRepository.findByIdOptional(request.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("País no encontrado con ID: " + request.getCountryId()));
            
            // Validar que exista la tarjeta (si se proporciona)
            Card card = null;
            if (request.getCardId() != null) {
                card = cardRepository.findByIdOptional(request.getCardId())
                        .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada con ID: " + request.getCardId()));
            }
            
            // Validar y obtener categoría si se proporciona
            Category category = null;
            if (request.getCategoryId() != null) {
                category = categoryRepository.findByIdOptional(request.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + request.getCategoryId()));
            }
            
            // Validar y obtener centro de costo si se proporciona
            CostCenter costCenter = null;
            if (request.getCostCenterId() != null) {
                costCenter = costCenterRepository.findByIdOptional(request.getCostCenterId())
                        .orElseThrow(() -> new IllegalArgumentException("Centro de costo no encontrado con ID: " + request.getCostCenterId()));
            }
            
            // 1. Crear la factura
            Invoice invoice = new Invoice();
            invoice.setUser(user);
            invoice.setCompany(company);
            invoice.setCountry(country);
            invoice.setCard(card);
            invoice.setPath(request.getPath());
            invoice.setFileName(request.getFileName());
            invoice.setStatus(InvoiceStatus.DRAFT);
            
            invoiceRepository.persist(invoice);
            logger.info("Invoice creado exitosamente con ID: " + invoice.getId());
            
            // 2. Crear el campo de factura
            InvoiceField invoiceField = new InvoiceField();
            invoiceField.invoice = invoice;
            invoiceField.vendorName = request.getVendorName();
            invoiceField.invoiceDate = request.getInvoiceDate();
            invoiceField.totalAmount = request.getTotalAmount();
            invoiceField.currency = request.getCurrency();
            invoiceField.concept = request.getConcept();
            invoiceField.category = category;
            invoiceField.costCenter = costCenter;
            invoiceField.clientVisited = request.getClientVisited();
            invoiceField.notes = request.getNotes();
            
            invoiceFieldRepository.persist(invoiceField);
            logger.info("InvoiceField creado exitosamente con ID: " + invoiceField.getId());
            
            // 3. Retornar DTO completo
            return convertToCompleteResponseDTO(invoice, invoiceField);
            
        } catch (Exception e) {
            logger.severe("Error al crear factura completa: " + e.getMessage());
            // La anotación @Transactional se encarga del rollback automático
            throw new RuntimeException("Error al crear factura completa: " + e.getMessage(), e);
        }
    }
    
    @Override
    /**
     * Actualiza un Invoice completo con InvoiceField en una sola transacción
     * No actualiza path, fileName, cardId ni status
     * Si algo falla, toda la operación se revierte automáticamente
     */
    @Transactional
    public Optional<CompleteInvoiceResponseDTO> updateCompleteInvoice(UpdateCompleteInvoiceRequest request) {
        logger.info("Actualizando factura completa - Invoice ID: " + request.getIdInvoice() + ", InvoiceField ID: " + request.getId());
        
        try {
            // 1. Validar que exista el Invoice
            Invoice invoice = invoiceRepository.findByIdOptional(request.getIdInvoice())
                    .orElseThrow(() -> new IllegalArgumentException("Invoice no encontrado con ID: " + request.getIdInvoice()));
            
            // 2. Validar que exista el InvoiceField
            InvoiceField invoiceField = invoiceFieldRepository.findByIdOptional(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("InvoiceField no encontrado con ID: " + request.getId()));
            
            // 3. Actualizar campos de Invoice (solo countryId)
            if (request.getCountryId() != null) {
                Country country = countryRepository.findByIdOptional(request.getCountryId())
                        .orElseThrow(() -> new IllegalArgumentException("País no encontrado con ID: " + request.getCountryId()));
                invoice.setCountry(country);
                logger.info("País actualizado para Invoice ID: " + invoice.getId());
            }
            
            invoice.setUpdatedAt(LocalDateTime.now());
            invoiceRepository.merge(invoice);
            logger.info("Invoice actualizado exitosamente con ID: " + invoice.getId());
            
            // 4. Actualizar campos de InvoiceField
            if (request.getVendorName() != null) {
                invoiceField.vendorName = request.getVendorName();
            }
            
            if (request.getInvoiceDate() != null) {
                invoiceField.invoiceDate = request.getInvoiceDate();
            }
            
            if (request.getTotalAmount() != null) {
                invoiceField.totalAmount = request.getTotalAmount();
            }
            
            if (request.getCurrency() != null) {
                invoiceField.currency = request.getCurrency();
            }
            
            if (request.getConcept() != null) {
                invoiceField.concept = request.getConcept();
            }
            
            if (request.getCategoryId() != null) {
                Category category = categoryRepository.findByIdOptional(request.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + request.getCategoryId()));
                invoiceField.category = category;
                logger.info("Categoría actualizada para InvoiceField ID: " + invoiceField.id);
            }
            
            if (request.getCostCenterId() != null) {
                CostCenter costCenter = costCenterRepository.findByIdOptional(request.getCostCenterId())
                        .orElseThrow(() -> new IllegalArgumentException("Centro de costo no encontrado con ID: " + request.getCostCenterId()));
                invoiceField.costCenter = costCenter;
                logger.info("Centro de costo actualizado para InvoiceField ID: " + invoiceField.id);
            }
            
            if (request.getClientVisited() != null) {
                invoiceField.clientVisited = request.getClientVisited();
            }
            
            if (request.getNotes() != null) {
                invoiceField.notes = request.getNotes();
            }
            
            invoiceField.updatedAt = LocalDateTime.now();
            invoiceFieldRepository.persist(invoiceField);
            logger.info("InvoiceField actualizado exitosamente con ID: " + invoiceField.id);
            
            // 5. Retornar DTO completo
            return Optional.of(convertToCompleteResponseDTO(invoice, invoiceField));
            
        } catch (IllegalArgumentException e) {
            logger.warning("Error de validación al actualizar factura completa: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.severe("Error al actualizar factura completa: " + e.getMessage());
            // La anotación @Transactional se encarga del rollback automático
            throw new RuntimeException("Error al actualizar factura completa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convierte Invoice e InvoiceField a CompleteInvoiceResponseDTO
     */
    private CompleteInvoiceResponseDTO convertToCompleteResponseDTO(Invoice invoice, InvoiceField invoiceField) {
        return new CompleteInvoiceResponseDTO(
                invoice.getId(),
                invoice.getUser() != null ? invoice.getUser().name : null,
                invoice.getCard() != null ? invoice.getCard().getMaskedCardNumber() : null,
                invoice.getCompany() != null ? invoice.getCompany().getName() : null,
                invoice.getCountry() != null ? invoice.getCountry().getName() : null,
                invoice.getPath(),
                invoice.getFileName(),
                invoice.getStatus(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt(),
                invoiceField.id,
                invoiceField.vendorName,
                invoiceField.invoiceDate,
                invoiceField.totalAmount,
                invoiceField.currency,
                invoiceField.concept,
                invoiceField.category != null ? invoiceField.category.name : null,
                invoiceField.costCenter != null ? invoiceField.costCenter.name : null,
                invoiceField.clientVisited,
                invoiceField.notes,
                invoiceField.createdAt,
                invoiceField.updatedAt
        );
    }
    
    /**
     * Convierte una entidad Invoice a InvoiceResponseDTO
     */
    private InvoiceResponseDTO convertToResponseDTO(Invoice invoice) {
        return new InvoiceResponseDTO(
                invoice.getId(),
                invoice.getUser() != null ? invoice.getUser().name : null,
                invoice.getCard() != null ? invoice.getCard().getMaskedCardNumber() : null,
                invoice.getCompany() != null ? invoice.getCompany().getName() : null,
                invoice.getCountry() != null ? invoice.getCountry().getName() : null,
                invoice.getPath(),
                invoice.getFileName(),
                invoice.getStatus(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }
}
