package org.datum.openkm.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
        title = "OpenKM Integration API",
        version = "1.0.0",
        description = "API REST para la integración con OpenKM. " +
                     "Permite la subida de imágenes y documentos a OpenKM de forma sencilla y eficiente. " +
                     "Soporta dos métodos de subida: Multipart Form Data y JSON con Base64.",
        contact = @Contact(
            name = "Datum Support Team",
            email = "support@datum.org",
            url = "https://datum.org"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8082",
            description = "Servidor de desarrollo local"
        ),
        @Server(
            url = "https://api.datum.org",
            description = "Servidor de producción"
        )
    },
    tags = {
        @Tag(
            name = "Image Upload",
            description = "Operaciones relacionadas con la subida de imágenes a OpenKM"
        ),
        @Tag(
            name = "Health",
            description = "Endpoints de verificación de estado del servicio"
        )
    }
)
public class OpenAPIConfig extends Application {
    // Esta clase solo necesita existir para activar la configuración de OpenAPI
}
