package org.datum.openkm.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.datum.openkm.dto.ImageUploadRequest;
import org.datum.openkm.dto.ImageUploadResponse;
import org.datum.openkm.dto.ErrorResponse;
import org.datum.openkm.service.ImageUploadService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;

@Path("/api/images")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Image Upload", description = "Endpoints para subida de imágenes a OpenKM")
public class ImageUploadController {

    private static final Logger LOG = Logger.getLogger(ImageUploadController.class);

    @Inject
    ImageUploadService imageUploadService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(
        summary = "Subir imagen a OpenKM",
        description = "Sube una imagen a OpenKM utilizando Multipart Form Data. Soporta imágenes de hasta 50MB."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Imagen subida exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ImageUploadResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Error de validación - Archivo inválido o parámetros incorrectos",
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
    public Response uploadImage(
            @RestForm("file") 
            @Parameter(description = "Archivo de imagen a subir", required = true)
            FileUpload file,
            
            @RestForm("fileName") 
            @Parameter(description = "Nombre del archivo en OpenKM", required = true)
            String fileName,
            
            @RestForm("destinationPath") 
            @Parameter(
                description = "Ruta de destino en OpenKM (ej: /okm:root/images)", 
                example = "/okm:root/images"
            )
            String destinationPath,
            
            @RestForm("description") 
            @Parameter(description = "Descripción del documento")
            String description,
            
            @RestForm("mimeType") 
            @Parameter(
                description = "Tipo MIME de la imagen", 
                example = "image/jpeg"
            )
            String mimeType) {
        try {
            LOG.infof("Recibiendo solicitud de subida de imagen: %s", fileName);

            ImageUploadRequest request = new ImageUploadRequest();
            request.setFileName(fileName != null ? fileName : file.fileName());
            request.setDestinationPath(destinationPath != null ? destinationPath : "/okm:root/images");
            request.setDescription(description);
            request.setMimeType(mimeType != null ? mimeType : file.contentType());

            // Leer el archivo y convertirlo a byte[]
            try {
                request.setImageData(Files.readAllBytes(file.filePath()));
            } catch (IOException e) {
                LOG.error("Error al leer el archivo", e);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error al leer el archivo: " + e.getMessage())
                        .build();
            }

            ImageUploadResponse response = imageUploadService.uploadImage(request);
            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (Exception e) {
            LOG.error("Error en el controlador de subida de imágenes", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al procesar la solicitud: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/upload/json")
    @Operation(
        summary = "Subir imagen a OpenKM usando JSON",
        description = "Sube una imagen a OpenKM utilizando JSON con datos en Base64. Útil para integraciones programáticas."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Imagen subida exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ImageUploadResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Error de validación - Datos inválidos o faltantes",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public Response uploadImageJson(
            @Valid 
            @RequestBody(
                description = "Datos de la imagen en formato JSON con contenido en Base64",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ImageUploadRequest.class)
                )
            )
            ImageUploadRequest request) {
        try {
            LOG.infof("Recibiendo solicitud JSON de subida de imagen: %s", request.getFileName());

            ImageUploadResponse response = imageUploadService.uploadImage(request);
            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (Exception e) {
            LOG.error("Error en el controlador de subida de imágenes (JSON)", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al procesar la solicitud: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/health")
    @Operation(
        summary = "Health Check",
        description = "Verifica que el servicio de subida de imágenes está activo y funcionando correctamente"
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
        return Response.ok("Image upload service is running").build();
    }
}
