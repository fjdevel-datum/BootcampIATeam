package org.datum.openkm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Solicitud para subir un documento Excel a OpenKM")
public class ExcelUploadRequest {

    @NotBlank(message = "El nombre del archivo es requerido")
    @Schema(description = "Nombre del archivo en OpenKM", example = "reporte-ventas.xlsx", required = true)
    private String fileName;

    @NotBlank(message = "La ruta de destino es requerida")
    @Schema(description = "Ruta de destino en OpenKM", example = "/okm:root/documentos/excel", required = true)
    private String destinationPath;

    @NotNull(message = "Los datos del documento son requeridos")
    @Schema(description = "Datos del documento Excel en formato byte array (Base64 para JSON)", required = true)
    private byte[] documentData;

    @Schema(description = "Descripción del documento", example = "Reporte de ventas del mes de octubre")
    private String description;

    @Schema(description = "Tipo MIME del documento Excel", example = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", defaultValue = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    private String mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // Constructors
    public ExcelUploadRequest() {
    }

    public ExcelUploadRequest(String fileName, String destinationPath, byte[] documentData) {
        this.fileName = fileName;
        this.destinationPath = destinationPath;
        this.documentData = documentData;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ExcelUploadRequest request = new ExcelUploadRequest();

        public Builder fileName(String fileName) {
            request.fileName = fileName;
            return this;
        }

        public Builder destinationPath(String destinationPath) {
            request.destinationPath = destinationPath;
            return this;
        }

        public Builder documentData(byte[] documentData) {
            request.documentData = documentData;
            return this;
        }

        public Builder description(String description) {
            request.description = description;
            return this;
        }

        public Builder mimeType(String mimeType) {
            request.mimeType = mimeType;
            return this;
        }

        public ExcelUploadRequest build() {
            return request;
        }
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public byte[] getDocumentData() {
        return documentData;
    }

    public void setDocumentData(byte[] documentData) {
        this.documentData = documentData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
