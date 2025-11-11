package org.datum.openkm.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.datum.openkm.dto.DownloadedDocument;
import org.datum.openkm.dto.ErrorResponse;
import org.datum.openkm.exception.ImageUploadException;
import org.datum.openkm.service.ImageUploadService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/api/documents")
@Tag(name = "Document Download", description = "Endpoints para descargar documentos de OpenKM")
public class DocumentController {

    private static final Logger LOG = Logger.getLogger(DocumentController.class);

    @Inject
    ImageUploadService imageUploadService;

    @GET
    @Path("/download")
    @Operation(
        summary = "Descargar documento de OpenKM",
        description = "Descarga un documento (imagen u otro archivo) desde OpenKM para visualización directa en el navegador. " +
                      "Retorna el contenido binario del archivo con el Content-Type apropiado."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Documento descargado exitosamente",
            content = @Content(
                mediaType = "application/octet-stream",
                schema = @Schema(implementation = byte[].class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Parámetro 'path' no proporcionado o inválido",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Documento no encontrado en OpenKM",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "Error interno del servidor o error al comunicarse con OpenKM",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public Response downloadDocument(
            @QueryParam("path")
            @Parameter(
                description = "Ruta completa del documento en OpenKM",
                example = "/okm:root/images/mi-foto.jpg",
                required = true
            )
            String path) throws ImageUploadException {
        
        LOG.infof("Recibiendo solicitud de descarga de documento: %s", path);

        // Validar que el parámetro path no sea nulo o vacío
        if (path == null || path.trim().isEmpty()) {
            LOG.error("El parámetro 'path' es requerido");
            throw new ImageUploadException(
                    "El parámetro 'path' es requerido",
                    Response.Status.BAD_REQUEST.getStatusCode()
            );
        }

        // Descargar el documento desde OpenKM (puede lanzar ImageUploadException u OpenKMException)
        DownloadedDocument document = imageUploadService.downloadDocument(path);

        LOG.infof("Enviando documento al cliente - Content-Type: %s, Tamaño: %d bytes",
                document.contentType(), document.size());

        // Construir la respuesta con el contenido binario
        return Response.ok(document.content())
                .type(document.contentType())
                .header("Content-Length", document.size())
                .header("Content-Disposition", "inline; filename=\"" + extractFileName(path) + "\"")
                .build();
    }

    /**
     * Extrae el nombre del archivo de una ruta completa.
     *
     * @param path Ruta completa del documento
     * @return Nombre del archivo
     */
    private String extractFileName(String path) {
        if (path == null || path.isEmpty()) {
            return "document";
        }
        
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < path.length() - 1) {
            return path.substring(lastSlash + 1);
        }
        
        return path;
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Health Check",
        description = "Verifica que el servicio de descarga de documentos está activo y funcionando correctamente"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Servicio activo y funcionando",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON
            )
        )
    })
    public Response healthCheck() {
        return Response.ok("Document download service is running").build();
    }
}
