package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.response.InvoiceDataResponse;
import com.datum.redsoft.dto.response.OCRResponseDTO;
import com.datum.redsoft.exception.InvoiceExtractionException;

/**
 * Interfaz para servicios de extracción de datos de facturas
 * Siguiendo el principio de Segregación de Interfaces (ISP)
 */
public interface InvoiceDataExtractionService {
    
    /**
     * Extrae datos estructurados de factura a partir de texto
     * 
     * @param extractedText texto extraído mediante OCR
     * @return datos de factura estructurados
     * @throws InvoiceExtractionException si hay error en la extracción
     */
    InvoiceDataResponse extractInvoiceData(String extractedText) throws InvoiceExtractionException;
    
    /**
     * Extrae datos básicos de factura optimizados para OCR
     * 
     * @param extractedText texto extraído mediante OCR
     * @return datos básicos de factura (vendor_name, invoice_date, total_amount, currency)
     * @throws InvoiceExtractionException si hay error en la extracción
     */
    OCRResponseDTO extractBasicInvoiceData(String extractedText) throws InvoiceExtractionException;
    
    /**
     * Verifica si el servicio de extracción está disponible
     * 
     * @return true si el servicio está disponible
     */
    boolean isServiceAvailable();
    
    /**
     * Obtiene el método de extracción utilizado por este servicio
     * 
     * @return nombre del método de extracción (ej: "AI", "REGEX")
     */
    String getExtractionMethod();
}