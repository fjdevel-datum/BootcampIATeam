package com.datum.redsoft.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para peticiones de análisis OCR
 * Representa los datos de entrada para el procesamiento de documentos
 */
public class OCRAnalysisRequest {
    
    @NotNull(message = "El tipo de contenido no puede ser nulo")
    @Size(min = 1, max = 100, message = "El tipo de contenido debe tener entre 1 y 100 caracteres")
    private String contentType;
    
    @NotNull(message = "El tamaño del archivo no puede ser nulo")
    private Long fileSize;
    
    private String fileName;

    // Constructor vacío
    public OCRAnalysisRequest() {}

    // Constructor
    public OCRAnalysisRequest(String contentType, Long fileSize, String fileName) {
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    // Getters y Setters
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "OCRAnalysisRequest{" +
                "contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}