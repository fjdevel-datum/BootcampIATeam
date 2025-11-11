package org.datum.openkm.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Record que encapsula el contenido de un documento descargado de OpenKM
 * junto con su tipo MIME.
 */
@Schema(description = "Documento descargado de OpenKM con su contenido y tipo MIME")
public record DownloadedDocument(
    
    @Schema(description = "Contenido del documento en bytes", required = true)
    byte[] content,
    
    @Schema(description = "Tipo MIME del documento", example = "image/jpeg", required = true)
    String contentType,
    
    @Schema(description = "Tamaño del documento en bytes", example = "245760")
    long size
) {
    /**
     * Constructor que calcula automáticamente el tamaño.
     */
    public DownloadedDocument(byte[] content, String contentType) {
        this(content, contentType, content != null ? content.length : 0);
    }
}
