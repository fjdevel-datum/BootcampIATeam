package org.datum.openkm.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.datum.openkm.client.OpenKMSDKClient;
import org.datum.openkm.config.OpenKMConfig;
import org.datum.openkm.dto.ImageUploadRequest;
import org.datum.openkm.dto.ImageUploadResponse;
import org.datum.openkm.exception.ImageUploadException;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Servicio para gestionar la subida de imágenes a OpenKM.
 * Utiliza Apache HTTP Client para comunicación con OpenKM.
 */
@ApplicationScoped
public class ImageUploadService {

    private static final Logger LOG = Logger.getLogger(ImageUploadService.class);

    @Inject
    OpenKMSDKClient openKMClient;

    @Inject
    OpenKMConfig openKMConfig;

    // Tipos MIME válidos para imágenes
    private static final Set<String> VALID_IMAGE_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp"
    );

    // Tamaño máximo: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * Sube una imagen a OpenKM.
     *
     * @param request Datos de la imagen a subir
     * @return Respuesta con la información del documento creado
     * @throws ImageUploadException si ocurre un error durante la subida
     */
    public ImageUploadResponse uploadImage(ImageUploadRequest request) {
        try {
            validateImage(request);

            String fullPath = buildFullPath(request.getDestinationPath(), request.getFileName());
            LOG.infof("=== Iniciando subida de imagen a OpenKM ===");
            LOG.infof("Ruta completa: %s", fullPath);
            LOG.infof("Tamaño: %d bytes", request.getImageData().length);
            LOG.infof("MIME Type: %s", request.getMimeType());

            // Subir a OpenKM usando HTTP Client
            var document = openKMClient.uploadDocument(
                    fullPath,
                    request.getImageData(),
                    request.getMimeType()
            );

            LOG.infof("Imagen subida exitosamente");
            LOG.infof("Document UUID: %s", document.getUuid());
            LOG.infof("Document Path: %s", document.getPath());
            LOG.infof("Document Author: %s", document.getAuthor());

            return ImageUploadResponse.builder()
                    .documentId(document.getUuid())
                    .fileName(request.getFileName())
                    .path(document.getPath())
                    .size(document.getSize() != null ? document.getSize() : (long) request.getImageData().length)
                    .mimeType(document.getMimeType())
                    .uploadDate(document.getCreated() != null ? document.getCreated() : LocalDateTime.now())
                    .message("Imagen subida exitosamente a OpenKM")
                    .success(true)
                    .build();

        } catch (ImageUploadException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error al subir imagen a OpenKM", e);
            throw new ImageUploadException(
                    "Error al subir la imagen: " + e.getMessage(),
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    e
            );
        }
    }

    /**
     * Valida los datos de la imagen antes de subirla.
     *
     * @param request Datos de la imagen a validar
     * @throws ImageUploadException si la validación falla
     */
    private void validateImage(ImageUploadRequest request) {
        if (request.getImageData() == null || request.getImageData().length == 0) {
            throw new ImageUploadException(
                    "Los datos de la imagen están vacíos",
                    Response.Status.BAD_REQUEST.getStatusCode()
            );
        }

        if (request.getImageData().length > MAX_FILE_SIZE) {
            throw new ImageUploadException(
                    String.format("El tamaño de la imagen excede el límite permitido de %d MB",
                            MAX_FILE_SIZE / (1024 * 1024)),
                    Response.Status.BAD_REQUEST.getStatusCode()
            );
        }

        if (request.getMimeType() != null && !isValidImageMimeType(request.getMimeType())) {
            throw new ImageUploadException(
                    "Tipo de imagen no válido. Formatos permitidos: JPEG, PNG, GIF, BMP, WEBP",
                    Response.Status.BAD_REQUEST.getStatusCode()
            );
        }
    }

    /**
     * Verifica si el tipo MIME es válido para una imagen.
     *
     * @param mimeType Tipo MIME a verificar
     * @return true si es un tipo MIME válido, false en caso contrario
     */
    private boolean isValidImageMimeType(String mimeType) {
        return VALID_IMAGE_MIME_TYPES.contains(mimeType.toLowerCase());
    }

    /**
     * Construye la ruta completa del archivo en OpenKM.
     *
     * @param destinationPath Directorio de destino
     * @param fileName Nombre del archivo
     * @return Ruta completa del archivo
     */
    private String buildFullPath(String destinationPath, String fileName) {
        String normalizedPath = destinationPath.startsWith("/") ? destinationPath : "/" + destinationPath;
        normalizedPath = normalizedPath.endsWith("/") ? normalizedPath : normalizedPath + "/";
        return normalizedPath + fileName;
    }

    /**
     * Verifica la conectividad con OpenKM.
     *
     * @return true si la conexión es exitosa
     */
    public boolean testConnection() {
        return openKMClient.testConnection();
    }
}

