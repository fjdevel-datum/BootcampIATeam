package org.datum.openkm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Representa un documento en OpenKM con su metadata.
 * Mapeado desde la respuesta XML del servicio REST de OpenKM.
 */
@Data
@Builder
public class OpenKMDocument {
    
    /**
     * UUID único del documento en OpenKM.
     */
    private String uuid;
    
    /**
     * Ruta completa del documento (ej: /okm:root/folder1/file.jpg).
     */
    private String path;
    
    /**
     * Usuario que creó el documento.
     */
    private String author;
    
    /**
     * Tipo MIME del documento.
     */
    private String mimeType;
    
    /**
     * Tamaño del documento en bytes.
     */
    private Long size;
    
    /**
     * Fecha de creación del documento.
     */
    private LocalDateTime created;
    
    /**
     * Checksum del contenido (para validación de integridad).
     */
    private String checksum;
    
    /**
     * Si el documento está bloqueado.
     */
    private Boolean locked;
    
    /**
     * Si el documento es convertible a PDF.
     */
    private Boolean convertibleToPdf;
}
