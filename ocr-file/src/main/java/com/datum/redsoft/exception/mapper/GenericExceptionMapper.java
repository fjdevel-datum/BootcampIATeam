package com.datum.redsoft.exception.mapper;

import com.datum.redsoft.dto.response.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

/**
 * Exception Mapper genérico para excepciones no manejadas
 * Proporciona un fallback para cualquier excepción no específicamente manejada
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    
    private static final Logger logger = Logger.getLogger(GenericExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(Exception exception) {
        logger.severe("Unhandled Exception: " + exception.getClass().getSimpleName() + " - " + exception.getMessage());
        
        // No revelar detalles internos en producción
        String details = isDevelopmentMode() ? exception.getMessage() : "Error interno del servidor";
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "Error interno del servidor",
            details
        );
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                      .entity(errorResponse)
                      .type(MediaType.APPLICATION_JSON)
                      .build();
    }
    
    /**
     * Determina si estamos en modo desarrollo
     * En un entorno real, esto se basaría en configuración
     */
    private boolean isDevelopmentMode() {
        // Simplificado: en producción esto debería venir de configuración
        String profile = System.getProperty("quarkus.profile", "dev");
        return "dev".equals(profile) || "test".equals(profile);
    }
}