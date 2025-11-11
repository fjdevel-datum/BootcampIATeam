package com.datum.redsoft;

import com.datum.redsoft.dto.response.OCRAnalysisResponse;
import com.datum.redsoft.dto.response.OCRResponseDTO;
import com.datum.redsoft.dto.response.InvoiceDataResponse;
import com.datum.redsoft.dto.response.StatusResponse;
import com.datum.redsoft.exception.OCRException;
import com.datum.redsoft.exception.InvoiceExtractionException;
import com.datum.redsoft.service.interfaces.OCRService;
import com.datum.redsoft.service.interfaces.InvoiceDataExtractionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Controlador REST para análisis OCR de facturas.
 * <p>Diseñado siguiendo principios SOLID:</p>
 * <ul>
 *   <li><b>SRP (Single Responsibility):</b> Solo maneja requests HTTP y coordinación de servicios</li>
 *   <li><b>DIP (Dependency Inversion):</b> Depende de abstracciones (interfaces), no de implementaciones concretas</li>
 *   <li><b>OCP (Open/Closed):</b> Abierto para extensión mediante nuevas implementaciones de servicios</li>
 * </ul>
 * 
 * @author Datum Redsoft
 * @version 1.0
 */
@Path("/api")
public class OCRController {
    
    private static final Logger logger = Logger.getLogger(OCRController.class.getName());

    @Inject
    OCRService ocrService;
    
    @Inject
    InvoiceDataExtractionService invoiceExtractionService;

    /**
     * Analiza una imagen o PDF de factura para extraer información estructurada.
     * <p>Coordina los servicios de OCR (extracción de texto) y de IA (análisis de datos),
     * retornando tanto el texto completo como los campos específicos de la factura.</p>
     * 
     * @param fileStream Stream del archivo de imagen/PDF
     * @param contentType Tipo MIME del archivo (image/jpeg, image/png, application/pdf, etc.)
     * @return OCRAnalysisResponse con texto extraído, datos estructurados y tiempo de procesamiento
     * @throws OCRException si hay error en la extracción de texto
     * @throws InvoiceExtractionException si hay error en el análisis de IA
     * @throws IOException si hay error al leer el archivo
     * @throws IllegalArgumentException si el archivo o Content-Type son inválidos
     * 
     * @apiNote POST /api/ocr
     *          Acepta: image/jpeg, image/png, image/tiff, image/bmp, application/pdf
     *          Produce: application/json
     */
    @POST
    @Path("/ocr")
    @Consumes({"image/jpeg", "image/png", "image/tiff", "image/bmp", "application/pdf"})
    @Produces(MediaType.APPLICATION_JSON)
    public OCRAnalysisResponse analyze(InputStream fileStream, @HeaderParam("Content-Type") String contentType) 
            throws OCRException, InvoiceExtractionException, IOException {
        
        logger.info("=== INICIO OCR REQUEST ===");
        logger.info("Content-Type recibido: " + contentType);
        
        validateInput(fileStream, contentType);
        byte[] imageData = inputStreamToByteArray(fileStream);
        validateFileData(imageData);

        logger.info("Tamaño del archivo: " + imageData.length + " bytes");
        
        long startTime = System.currentTimeMillis();
        
        String extractedText = ocrService.extractTextFromImage(imageData);
        logger.info("Texto extraído exitosamente. Longitud: " + extractedText.length());
        
        OCRResponseDTO invoiceData = invoiceExtractionService.extractBasicInvoiceData(extractedText);
        logger.info("Datos de factura extraídos exitosamente");
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        OCRAnalysisResponse response = new OCRAnalysisResponse(
            extractedText,
            invoiceData,
            processingTime
        );
        
        logger.info("=== FIN OCR REQUEST ===");
        return response;
    }
    
    /**
     * Verifica el estado de salud de los servicios OCR y de extracción.
     * 
     * @return StatusResponse con disponibilidad de cada servicio y método de extracción
     * @apiNote GET /api/status
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResponse getStatus() {
        boolean ocrAvailable = ocrService.isServiceAvailable();
        boolean extractionAvailable = invoiceExtractionService.isServiceAvailable();
        
        String status = (ocrAvailable && extractionAvailable) ? "healthy" : "degraded";
        
        return new StatusResponse(
            status,
            ocrAvailable,
            extractionAvailable,
            invoiceExtractionService.getExtractionMethod()
        );
    }
    
    /**
     * Convierte un InputStream a array de bytes para procesamiento.
     * 
     * @param inputStream Stream de entrada del archivo
     * @return Array de bytes del contenido completo
     * @throws IOException si hay error al leer el stream
     */
    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Valida que el archivo y el Content-Type sean válidos.
     * 
     * @param fileStream Stream del archivo a validar
     * @param contentType Tipo MIME del archivo
     * @throws IllegalArgumentException si el archivo es null o el Content-Type no es soportado
     */
    private void validateInput(InputStream fileStream, String contentType) {
        if (fileStream == null) {
            throw new IllegalArgumentException("No se recibió archivo");
        }
        
        if (contentType == null || contentType.isEmpty()) {
            logger.warning("Content-Type no especificado, asumiendo image/jpeg");
        } else {
            validateContentType(contentType);
        }
    }
    
    /**
     * Valida que el Content-Type esté en la lista de tipos soportados.
     * 
     * @param contentType Tipo MIME a validar
     * @throws IllegalArgumentException si el tipo no es soportado
     */
    private void validateContentType(String contentType) {
        String[] allowedTypes = {
            "image/jpeg", "image/png", "image/tiff", "image/bmp", 
            "application/pdf"
        };
        
        boolean isValid = false;
        for (String allowedType : allowedTypes) {
            if (contentType.toLowerCase().contains(allowedType.toLowerCase())) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new IllegalArgumentException("Tipo de archivo no soportado: " + contentType);
        }
    }
    
    /**
     * Valida el tamaño del archivo para asegurar que esté dentro de los límites aceptables.
     * 
     * @param imageData Datos del archivo en bytes
     * @throws IllegalArgumentException si el archivo está vacío, es muy pequeño (&lt;1KB) o muy grande (&gt;10MB)
     */
    private void validateFileData(byte[] imageData) {
        if (imageData.length == 0) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        final int MAX_FILE_SIZE = 10 * 1024 * 1024;
        if (imageData.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo es demasiado grande. Máximo permitido: 10MB");
        }
        
        final int MIN_FILE_SIZE = 1024;
        if (imageData.length < MIN_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo es demasiado pequeño. Mínimo requerido: 1KB");
        }
    }
}
