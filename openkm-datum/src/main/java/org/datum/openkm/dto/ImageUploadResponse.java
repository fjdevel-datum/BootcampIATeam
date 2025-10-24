package org.datum.openkm.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta exitosa de la subida de imagen")
public class ImageUploadResponse {

    @Schema(description = "ID del documento en OpenKM", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String documentId;

    @Schema(description = "Nombre del archivo", example = "mi-imagen.jpg")
    private String fileName;

    @Schema(description = "Ruta completa del documento en OpenKM", example = "/okm:root/images/mi-imagen.jpg")
    private String path;

    @Schema(description = "Tamaño del archivo en bytes", example = "245760")
    private Long size;

    @Schema(description = "Tipo MIME de la imagen", example = "image/jpeg")
    private String mimeType;

    @Schema(description = "Fecha y hora de la subida", example = "2025-10-23T15:30:45")
    private LocalDateTime uploadDate;

    @Schema(description = "Mensaje de respuesta", example = "Imagen subida exitosamente")
    private String message;

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean success;

    // Constructors
    public ImageUploadResponse() {
    }

    public ImageUploadResponse(String documentId, String fileName, String path) {
        this.documentId = documentId;
        this.fileName = fileName;
        this.path = path;
        this.success = true;
        this.uploadDate = LocalDateTime.now();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ImageUploadResponse response = new ImageUploadResponse();

        public Builder documentId(String documentId) {
            response.documentId = documentId;
            return this;
        }

        public Builder fileName(String fileName) {
            response.fileName = fileName;
            return this;
        }

        public Builder path(String path) {
            response.path = path;
            return this;
        }

        public Builder size(Long size) {
            response.size = size;
            return this;
        }

        public Builder mimeType(String mimeType) {
            response.mimeType = mimeType;
            return this;
        }

        public Builder uploadDate(LocalDateTime uploadDate) {
            response.uploadDate = uploadDate;
            return this;
        }

        public Builder message(String message) {
            response.message = message;
            return this;
        }

        public Builder success(boolean success) {
            response.success = success;
            return this;
        }

        public ImageUploadResponse build() {
            return response;
        }
    }

    // Getters and Setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
