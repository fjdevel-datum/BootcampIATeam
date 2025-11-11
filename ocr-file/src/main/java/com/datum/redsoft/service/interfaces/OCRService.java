package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.exception.OCRException;

/**
 * Interfaz para servicios de OCR (Optical Character Recognition)
 * Siguiendo el principio de Segregación de Interfaces (ISP)
 */
public interface OCRService {
    
    /**
     * Extrae texto de una imagen usando OCR
     * 
     * @param imageData bytes de la imagen a procesar
     * @return texto extraído de la imagen
     * @throws OCRException si hay error en el procesamiento OCR
     */
    String extractTextFromImage(byte[] imageData) throws OCRException;
    
    /**
     * Verifica si el servicio OCR está disponible
     * 
     * @return true si el servicio está disponible
     */
    boolean isServiceAvailable();
}