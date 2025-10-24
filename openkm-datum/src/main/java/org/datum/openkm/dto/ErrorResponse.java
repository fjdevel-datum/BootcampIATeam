package org.datum.openkm.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Respuesta de error estándar")
public class ErrorResponse {

    @Schema(description = "Mensaje de error", example = "Error al subir la imagen")
    private String message;

    @Schema(description = "Código de estado HTTP", example = "500")
    private int statusCode;

    @Schema(description = "Fecha y hora del error", example = "2025-10-23T15:30:45")
    private LocalDateTime timestamp;

    @Schema(description = "Ruta del endpoint que generó el error", example = "/api/images/upload")
    private String path;

    @Schema(description = "Lista de detalles adicionales del error")
    private List<String> details;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.details = new ArrayList<>();
    }

    public ErrorResponse(String message, int statusCode) {
        this();
        this.message = message;
        this.statusCode = statusCode;
    }

    public ErrorResponse(String message, int statusCode, String path) {
        this(message, statusCode);
        this.path = path;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void addDetail(String detail) {
        this.details.add(detail);
    }
}
