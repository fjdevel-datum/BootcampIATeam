package org.datum.openkm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Solicitud para subir una imagen a OpenKM")
public class ImageUploadRequest {

    @NotBlank(message = "El nombre del archivo es requerido")
    @Schema(description = "Nombre del archivo en OpenKM", example = "mi-imagen.jpg", required = true)
    private String fileName;

    @NotBlank(message = "La ruta de destino es requerida")
    @Schema(description = "Ruta de destino en OpenKM", example = "/okm:root/images", required = true)
    private String destinationPath;

    @NotNull(message = "Los datos de la imagen son requeridos")
    @Schema(description = "Datos de la imagen en formato byte array (Base64 para JSON)", required = true)
    private byte[] imageData;

    @Schema(description = "Descripción del documento", example = "Imagen del proyecto X")
    private String description;

    @Schema(description = "Tipo MIME de la imagen", example = "image/jpeg")
    private String mimeType;

    // Constructors
    public ImageUploadRequest() {
    }

    public ImageUploadRequest(String fileName, String destinationPath, byte[] imageData) {
        this.fileName = fileName;
        this.destinationPath = destinationPath;
        this.imageData = imageData;
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

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
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
