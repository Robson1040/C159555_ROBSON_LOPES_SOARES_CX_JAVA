package br.gov.caixa.api.investimentos.exception.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import br.gov.caixa.api.investimentos.exception.auth.AccessDeniedException;

/**
 * Handler global para RuntimeExceptions gerais
 * Trata erros de regras de negócio e exceções não mapeadas
 */
@Provider
public class BusinessExceptionHandler implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        // Log da exceção para debugging
        System.err.println("BusinessException: " + exception.getMessage());
        exception.printStackTrace();

        // Identifica tipo de erro baseado na exceção específica
        if (exception instanceof AccessDeniedException) {
            return createForbiddenResponse(exception);
        } else if (isValidationError(exception)) {
            return createValidationErrorResponse(exception);
        } else if (isNotFoundError(exception)) {
            return createNotFoundResponse(exception);
        } else {
            return createGenericErrorResponse(exception);
        }
    }

    private boolean isValidationError(RuntimeException exception) {
        String message = exception.getMessage().toLowerCase();
        return message.contains("inválido") || 
               message.contains("dados inválidos") ||
               message.contains("validation") ||
			   message.contains("não devem") ||
			   message.contains("devem") ||
			   message.contains("precisa") ||
               message.contains("deve ser") || message.contains("já existe ") ||
               message.contains("não pode ser");
    }

    private boolean isNotFoundError(RuntimeException exception) {
        String message = exception.getMessage().toLowerCase();
        return message.contains("não encontrad") || 
               message.contains("not found") ||
               message.contains("nenhum produto encontrado");
    }

    private Response createValidationErrorResponse(RuntimeException exception) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(
                "Erro de validação: " + exception.getMessage()
        );
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }

    private Response createNotFoundResponse(RuntimeException exception) {
        ErrorResponse errorResponse = ErrorResponse.notFound(exception.getMessage());
        
        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .build();
    }

    private Response createForbiddenResponse(RuntimeException exception) {
        ErrorResponse errorResponse = ErrorResponse.forbidden(exception.getMessage());
        
        return Response.status(Response.Status.FORBIDDEN)
                .entity(errorResponse)
                .build();
    }

    private Response createGenericErrorResponse(RuntimeException exception) {
        ErrorResponse errorResponse = ErrorResponse.internalError(
                "Erro interno do servidor: " + exception.getMessage()
        );
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }
}