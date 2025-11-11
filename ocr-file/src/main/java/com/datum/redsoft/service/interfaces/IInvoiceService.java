package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.request.CreateInvoiceRequest;
import com.datum.redsoft.dto.request.UpdateInvoiceRequest;
import com.datum.redsoft.dto.request.CreateCompleteInvoiceRequest;
import com.datum.redsoft.dto.response.InvoiceResponseDTO;
import com.datum.redsoft.dto.response.CompleteInvoiceResponseDTO;
import com.datum.redsoft.enums.InvoiceStatus;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de facturas
 * Define los contratos para la gestión de facturas
 */
public interface IInvoiceService {
    
    /**
     * Obtiene todas las facturas
     */
    List<InvoiceResponseDTO> getAllInvoices();
    
    /**
     * Obtiene una factura por ID
     */
    Optional<InvoiceResponseDTO> getInvoiceById(Long id);
    
    /**
     * Obtiene facturas por usuario
     */
    List<InvoiceResponseDTO> getInvoicesByUser(Long userId);
    
    /**
     * Obtiene facturas por empresa
     */
    List<InvoiceResponseDTO> getInvoicesByCompany(Long companyId);
    
    /**
     * Obtiene facturas por tarjeta
     */
    List<InvoiceResponseDTO> getInvoicesByCard(Long cardId);
    
    /**
     * Obtiene facturas por país
     */
    List<InvoiceResponseDTO> getInvoicesByCountry(Long countryId);
    
    /**
     * Obtiene facturas por estado
     */
    List<InvoiceResponseDTO> getInvoicesByStatus(InvoiceStatus status);
    
    /**
     * Obtiene facturas en estado borrador
     */
    List<InvoiceResponseDTO> getDraftInvoices();
    
    /**
     * Obtiene facturas pendientes
     */
    List<InvoiceResponseDTO> getPendingInvoices();
    
    /**
     * Obtiene facturas procesadas
     */
    List<InvoiceResponseDTO> getProcessedInvoices();
    
    /**
     * Obtiene facturas aprobadas
     */
    List<InvoiceResponseDTO> getApprovedInvoices();
    
    /**
     * Crea una nueva factura
     */
    InvoiceResponseDTO createInvoice(CreateInvoiceRequest request);
    
    /**
     * Crea una factura completa con InvoiceField en una sola transacción
     */
    CompleteInvoiceResponseDTO createCompleteInvoice(CreateCompleteInvoiceRequest request);
    
    /**
     * Actualiza una factura existente
     */
    Optional<InvoiceResponseDTO> updateInvoice(Long id, UpdateInvoiceRequest request);
    
    /**
     * Actualiza una factura completa con InvoiceField en una sola transacción
     * No actualiza path, fileName, cardId ni status
     */
    Optional<CompleteInvoiceResponseDTO> updateCompleteInvoice(com.datum.redsoft.dto.request.UpdateCompleteInvoiceRequest request);
    
    /**
     * Elimina una factura
     */
    boolean deleteInvoice(Long id);
    
    /**
     * Cambia el estado de una factura
     */
    boolean changeInvoiceStatus(Long id, InvoiceStatus status);
    
    /**
     * Marca una factura como procesada
     */
    boolean processInvoice(Long id);
    
    /**
     * Aprueba una factura
     */
    boolean approveInvoice(Long id);
    
    /**
     * Rechaza una factura
     */
    boolean rejectInvoice(Long id);
    
    /**
     * Marca una factura como pagada
     */
    boolean markAsPaid(Long id);
    
    /**
     * Cancela una factura
     */
    boolean cancelInvoice(Long id);
}
