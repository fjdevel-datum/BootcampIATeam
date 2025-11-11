package com.datum.redsoft.exception.mapper;

import com.datum.redsoft.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

/**
 * Exception Mapper para errores de validación
 * Convierte ConstraintViolationException en respuestas HTTP estandarizadas
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    
    private static final Logger logger = Logger.getLogger(ValidationExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        logger.warning("Validation Exception: " + exception.getMessage());
        
        // Crear un mensaje detallado con todas las violaciones
        StringBuilder details = new StringBuilder();
        exception.getConstraintViolations().forEach(violation -> {
            details.append(violation.getPropertyPath())
                   .append(": ")
                   .append(violation.getMessage())
                   .append("; ");
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Error de validación en los datos enviados",
            details.toString()
        );
        
        return Response.status(Response.Status.BAD_REQUEST)
                      .entity(errorResponse)
                      .type(MediaType.APPLICATION_JSON)
                      .build();
    }
}