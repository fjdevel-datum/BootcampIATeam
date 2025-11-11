package com.datum.redsoft.service;

import com.datum.redsoft.dto.request.CreateInvoiceFieldRequest;
import com.datum.redsoft.dto.request.UpdateInvoiceFieldRequest;
import com.datum.redsoft.dto.response.InvoiceFieldResponseDTO;
import com.datum.redsoft.entity.Category;
import com.datum.redsoft.entity.CostCenter;
import com.datum.redsoft.entity.Invoice;
import com.datum.redsoft.entity.InvoiceField;
import com.datum.redsoft.repository.CategoryRepository;
import com.datum.redsoft.repository.CostCenterRepository;
import com.datum.redsoft.repository.InvoiceFieldRepository;
import com.datum.redsoft.repository.InvoiceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar InvoiceField
 * Maneja los datos extraídos del OCR y procesados por LLM
 */
@ApplicationScoped
public class InvoiceFieldService {
    
    private static final Logger logger = Logger.getLogger(InvoiceFieldService.class.getName());
    
    @Inject
    InvoiceFieldRepository invoiceFieldRepository;
    
    @Inject
    InvoiceRepository invoiceRepository;
    
    @Inject
    CategoryRepository categoryRepository;
    
    @Inject
    CostCenterRepository costCenterRepository;
    
    /**
     * Obtiene todos los campos de facturas
     */
    public List<InvoiceFieldResponseDTO> getAllInvoiceFields() {
        logger.info("Obteniendo todos los campos de facturas");
        return invoiceFieldRepository.listAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un campo de factura por ID
     */
    public Optional<InvoiceFieldResponseDTO> getInvoiceFieldById(Long id) {
        logger.info("Buscando campo de factura con ID: " + id);
        return invoiceFieldRepository.findByIdOptional(id)
                .map(this::convertToResponseDTO);
    }
    
    /**
     * Obtiene el campo de factura por ID de factura
     */
    public Optional<InvoiceFieldResponseDTO> getInvoiceFieldByInvoiceId(Long invoiceId) {
        logger.info("Buscando campo de factura para invoice ID: " + invoiceId);
        return invoiceFieldRepository.findByInvoiceId(invoiceId)
                .map(this::convertToResponseDTO);
    }
    
    /**
     * Obtiene campos de facturas por proveedor
     */
    public List<InvoiceFieldResponseDTO> getInvoiceFieldsByVendor(String vendorName) {
        logger.info("Buscando campos de facturas del proveedor: " + vendorName);
        return invoiceFieldRepository.findByVendorName(vendorName).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea un nuevo campo de factura
     */
    @Transactional
    public InvoiceFieldResponseDTO createInvoiceField(CreateInvoiceFieldRequest request) {
        logger.info("Creando nuevo campo de factura para invoice ID: " + request.getInvoiceId());
        
        // Validar que exista la factura
        Invoice invoice = invoiceRepository.findByIdOptional(request.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con ID: " + request.getInvoiceId()));
        
        // Verificar que no exista ya un campo para esta factura
        if (invoiceFieldRepository.findByInvoiceId(request.getInvoiceId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un campo de factura para la factura ID: " + request.getInvoiceId());
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
        
        // Crear el campo de factura
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
        logger.info("Campo de factura creado exitosamente con ID: " + invoiceField.getId());
        
        return convertToResponseDTO(invoiceField);
    }
    
    /**
     * Actualiza un campo de factura existente
     */
    @Transactional
    public Optional<InvoiceFieldResponseDTO> updateInvoiceField(Long id, UpdateInvoiceFieldRequest request) {
        logger.info("Actualizando campo de factura con ID: " + id);
        
        Optional<InvoiceField> invoiceFieldOpt = invoiceFieldRepository.findByIdOptional(id);
        if (invoiceFieldOpt.isEmpty()) {
            logger.warning("Campo de factura no encontrado con ID: " + id);
            return Optional.empty();
        }
        
        InvoiceField invoiceField = invoiceFieldOpt.get();
        
        // Actualizar campos proporcionados
        updateInvoiceFieldData(invoiceField, request);
        
        invoiceFieldRepository.persist(invoiceField);
        logger.info("Campo de factura actualizado exitosamente con ID: " + id);
        
        return Optional.of(convertToResponseDTO(invoiceField));
    }
    
    /**
     * Elimina un campo de factura
     */
    @Transactional
    public boolean deleteInvoiceField(Long id) {
        logger.info("Eliminando campo de factura con ID: " + id);
        
        Optional<InvoiceField> invoiceFieldOpt = invoiceFieldRepository.findByIdOptional(id);
        if (invoiceFieldOpt.isEmpty()) {
            logger.warning("Campo de factura no encontrado con ID: " + id);
            return false;
        }
        
        invoiceFieldRepository.delete(invoiceFieldOpt.get());
        logger.info("Campo de factura eliminado exitosamente");
        return true;
    }
    
    /**
     * Actualiza los datos del campo de factura
     */
    private void updateInvoiceFieldData(InvoiceField invoiceField, UpdateInvoiceFieldRequest request) {
        if (request.getVendorName() != null && !request.getVendorName().trim().isEmpty()) {
            invoiceField.vendorName = request.getVendorName().trim();
        }
        if (request.getInvoiceDate() != null) {
            invoiceField.invoiceDate = request.getInvoiceDate();
        }
        if (request.getTotalAmount() != null) {
            invoiceField.totalAmount = request.getTotalAmount();
        }
        if (request.getCurrency() != null && !request.getCurrency().trim().isEmpty()) {
            invoiceField.currency = request.getCurrency().trim();
        }
        if (request.getConcept() != null) {
            invoiceField.concept = request.getConcept().trim();
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findByIdOptional(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + request.getCategoryId()));
            invoiceField.category = category;
        }
        if (request.getCostCenterId() != null) {
            CostCenter costCenter = costCenterRepository.findByIdOptional(request.getCostCenterId())
                    .orElseThrow(() -> new IllegalArgumentException("Centro de costo no encontrado con ID: " + request.getCostCenterId()));
            invoiceField.costCenter = costCenter;
        }
        if (request.getClientVisited() != null) {
            invoiceField.clientVisited = request.getClientVisited().trim();
        }
        if (request.getNotes() != null) {
            invoiceField.notes = request.getNotes().trim();
        }
    }
    
    /**
     * Convierte una entidad InvoiceField a InvoiceFieldResponseDTO
     */
    private InvoiceFieldResponseDTO convertToResponseDTO(InvoiceField invoiceField) {
        return new InvoiceFieldResponseDTO(
                invoiceField.id,
                invoiceField.invoice != null ? invoiceField.invoice.id : null,
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
}