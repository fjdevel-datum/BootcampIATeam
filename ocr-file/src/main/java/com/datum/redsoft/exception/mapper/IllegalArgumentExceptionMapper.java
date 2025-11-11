package com.datum.redsoft.exception.mapper;

import com.datum.redsoft.dto.response.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

/**
 * Exception Mapper para errores de argumentos inválidos
 * Convierte IllegalArgumentException en respuestas HTTP estandarizadas
 */
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    
    private static final Logger logger = Logger.getLogger(IllegalArgumentExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        logger.warning("Illegal Argument Exception: " + exception.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_ARGUMENT",
            "Argumento inválido",
            exception.getMessage()
        );
        
        return Response.status(Response.Status.BAD_REQUEST)
                      .entity(errorResponse)
                      .type(MediaType.APPLICATION_JSON)
                      .build();
    }
}