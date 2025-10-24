package org.datum.openkm.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.datum.openkm.dto.ErrorResponse;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Excepción capturada", exception);

        if (exception instanceof OpenKMException openKMException) {
            return handleOpenKMException(openKMException);
        }

        if (exception instanceof ConstraintViolationException constraintException) {
            return handleConstraintViolationException(constraintException);
        }

        return handleGenericException(exception);
    }

    private Response handleOpenKMException(OpenKMException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                exception.getStatusCode()
        );
        return Response.status(exception.getStatusCode()).entity(errorResponse).build();
    }

    private Response handleConstraintViolationException(ConstraintViolationException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Error de validación",
                Response.Status.BAD_REQUEST.getStatusCode()
        );

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            errorResponse.addDetail(violation.getPropertyPath() + ": " + violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
    }

    private Response handleGenericException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Error interno del servidor: " + exception.getMessage(),
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }
}
