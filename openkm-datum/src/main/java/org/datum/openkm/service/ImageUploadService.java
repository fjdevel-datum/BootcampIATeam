package org.datum.openkm.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.datum.openkm.client.OpenKMSDKClient;
import org.datum.openkm.config.OpenKMConfig;
import org.datum.openkm.dto.DownloadedDocument;
import org.datum.openkm.dto.ExcelUploadRequest;
import org.datum.openkm.dto.ImageUploadRequest;
import org.datum.openkm.dto.ImageUploadResponse;
import org.datum.openkm.exception.ImageUploadException;
import org.datum.openkm.exception.OpenKMException;
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

    // Tipos MIME válidos para Excel
    private static final Set<String> VALID_EXCEL_MIME_TYPES = Set.of(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
            "application/vnd.ms-excel" // .xls
    );

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
     * Sube un documento Excel a OpenKM.
     *
     * @param request Datos del documento Excel a subir
     * @return Respuesta con la información del documento creado
     * @throws ImageUploadException si ocurre un error durante la subida
     */
    public ImageUploadResponse uploadExcelDocument(ExcelUploadRequest request) {
        try {
            validateExcelDocument(request);

            String fullPath = buildFullPath(request.getDestinationPath(), request.getFileName());
            LOG.infof("=== Iniciando subida de documento Excel a OpenKM ===");
            LOG.infof("Ruta completa: %s", fullPath);
            LOG.infof("Tamaño: %d bytes", request.getDocumentData().length);
            LOG.infof("MIME Type: %s", request.getMimeType());

            // Subir a OpenKM usando HTTP Client
            var document = openKMClient.uploadDocument(
                    fullPath,
                    request.getDocumentData(),
                    request.getMimeType()
            );

            LOG.infof("Documento Excel subido exitosamente");
            LOG.infof("Document UUID: %s", document.getUuid());
            LOG.infof("Document Path: %s", document.getPath());
            LOG.infof("Document Author: %s", document.getAuthor());

            return ImageUploadResponse.builder()
                    .documentId(document.getUuid())
                    .fileName(request.getFileName())
                    .path(document.getPath())
                    .size(document.getSize() != null ? document.getSize() : (long) request.getDocumentData().length)
                    .mimeType(document.getMimeType())
                    .uploadDate(document.getCreated() != null ? document.getCreated() : LocalDateTime.now())
                    .message("Documento Excel subido exitosamente a OpenKM")
                    .success(true)
                    .build();

        } catch (ImageUploadException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error al subir documento Excel a OpenKM", e);
            throw new ImageUploadException(
                    "Error al subir el documento Excel: " + e.getMessage(),
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    e
            );
        }
    }

    /**
     * Valida los datos del documento Excel antes de subirlo.
     *
     * @param request Datos del documento a validar
     * @throws ImageUploadException si la validación falla
     */
    private void validateExcelDocument(ExcelUploadRequest request) {
        if (request.getDocumentData() == null || request.getDocumentData().length == 0) {
            throw new ImageUploadException(
                    "Los datos del documento están vacíos",
                    Response.Status.BAD_REQUEST.getStatusCode()
            );
        }

        if (request.getDocumentData().length > MAX_FILE_SIZE) {
            throw new ImageUploadException(
                    String.format("El tamaño del documento excede el límite permitido de %d MB",
                            MAX_FILE_SIZE / (1024 * 1024)),
                    Response.Status.BAD_REQUEST.getStatusCode()
            );
        }

        if (request.getMimeType() != null && !isValidExcelMimeType(request.getMimeType())) {
            throw new ImageUploadException(
                    "Tipo de documento no válido. Formatos permitidos: .xlsx, .xls",
                    Response.Status.BAD_REQUEST.getStatusCode()
            );
        }
    }

    /**
     * Verifica si el tipo MIME es válido para un documento Excel.
     *
     * @param mimeType Tipo MIME a verificar
     * @return true si es un tipo MIME válido, false en caso contrario
     */
    private boolean isValidExcelMimeType(String mimeType) {
        return VALID_EXCEL_MIME_TYPES.contains(mimeType.toLowerCase());
    }

    /**
     * Verifica la conectividad con OpenKM.
     *
     * @return true si la conexión es exitosa
     */
    public boolean testConnection() {
        return openKMClient.testConnection();
    }

    /**
     * Descarga un documento desde OpenKM.
     *
     * @param docPath Ruta completa del documento en OpenKM
     * @return DownloadedDocument con el contenido y tipo MIME
     * @throws ImageUploadException si ocurre un error durante la descarga
     * @throws OpenKMException si el documento no existe o hay un error en OpenKM
     */
    public DownloadedDocument downloadDocument(String docPath) throws ImageUploadException {
        LOG.infof("=== Descargando documento desde OpenKM ===");
        LOG.infof("Ruta: %s", docPath);

        try {
            // Descargar desde OpenKM usando HTTP Client
            DownloadedDocument document = openKMClient.downloadDocument(docPath);

            LOG.infof("Documento descargado exitosamente");
            LOG.infof("Content-Type: %s", document.contentType());
            LOG.infof("Tamaño: %d bytes", document.size());

            return document;

        } catch (OpenKMException e) {
            // Re-lanzar excepciones de OpenKM (404, 500, etc.)
            LOG.errorf("Error de OpenKM al descargar documento: %s", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.errorf("Error inesperado al descargar documento: %s", e.getMessage());
            throw new ImageUploadException(
                    "Error al descargar el documento: " + e.getMessage(),
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    e
            );
        }
    }
}

