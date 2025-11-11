package com.datum.redsoft.exception.mapper;

import com.datum.redsoft.dto.response.ErrorResponse;
import com.datum.redsoft.exception.InvoiceExtractionException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

/**
 * Exception Mapper para errores de extracción de facturas
 * Convierte InvoiceExtractionException en respuestas HTTP estandarizadas
 */
@Provider
public class InvoiceExtractionExceptionMapper implements ExceptionMapper<InvoiceExtractionException> {
    
    private static final Logger logger = Logger.getLogger(InvoiceExtractionExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(InvoiceExtractionException exception) {
        logger.severe("Invoice Extraction Exception: " + exception.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "EXTRACTION_ERROR",
            "Error en la extracción de datos de factura",
            exception.getMessage()
        );
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                      .entity(errorResponse)
                      .type(MediaType.APPLICATION_JSON)
                      .build();
    }
}