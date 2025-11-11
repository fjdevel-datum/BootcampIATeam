package com.datum.redsoft.exception.mapper;

import com.datum.redsoft.dto.response.ErrorResponse;
import com.datum.redsoft.exception.OCRException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

/**
 * Exception Mapper para errores de OCR
 * Convierte OCRException en respuestas HTTP estandarizadas
 */
@Provider
public class OCRExceptionMapper implements ExceptionMapper<OCRException> {
    
    private static final Logger logger = Logger.getLogger(OCRExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(OCRException exception) {
        logger.severe("OCR Exception: " + exception.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "OCR_ERROR",
            "Error en el procesamiento OCR",
            exception.getMessage()
        );
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                      .entity(errorResponse)
                      .type(MediaType.APPLICATION_JSON)
                      .build();
    }
}