package br.gov.caixa.api.investimentos.exception.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;


@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> violationMessages = exception.getConstraintViolations()
                .stream()
                .map(this::formatViolationMessage)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.validationError(
                "Dados inválidos fornecidos", 
                violationMessages
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }

    private String formatViolationMessage(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        Object invalidValue = violation.getInvalidValue();
        
        
        String fieldName = propertyPath.contains(".") ? 
                propertyPath.substring(propertyPath.lastIndexOf('.') + 1) : 
                propertyPath;
        
        return String.format("Campo '%s': %s (valor fornecido: %s)", 
                fieldName, message, invalidValue);
    }
}