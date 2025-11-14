package org.example.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.example.dto.ErrorResponse;

/**
 * Handler específico para WebApplicationException
 * Trata erros de JSON malformado e outras exceções HTTP
 */
@Provider
public class WebApplicationExceptionHandler implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        // Log da exceção para debugging
        System.err.println("WebApplicationException: " + exception.getMessage());
        exception.printStackTrace();

        // Se é erro 400 (Bad Request), mantém como 400
        if (exception.getResponse().getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            ErrorResponse errorResponse = ErrorResponse.badRequest(
                    "Dados da requisição inválidos: formato JSON incorreto"
            );
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        }
        
        // Para outros casos, verifica se é erro de parsing JSON
        if (isJsonParsingError(exception)) {
            ErrorResponse errorResponse = ErrorResponse.badRequest(
                    "Formato JSON inválido"
            );
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        }

        // Para outros WebApplicationException, mantém o status original mas com formato padronizado
        ErrorResponse errorResponse = new ErrorResponse(
                "Erro na requisição: " + exception.getMessage(),
                exception.getResponse().getStatus()
        );
        
        return Response.status(exception.getResponse().getStatus())
                .entity(errorResponse)
                .build();
    }

    private boolean isJsonParsingError(WebApplicationException exception) {
        String message = exception.getMessage().toLowerCase();
        return message.contains("json") || 
               message.contains("parsing") ||
               message.contains("unexpected end-of-input") ||
               message.contains("malformed") ||
               exception.getCause() != null && 
               exception.getCause().getClass().getName().contains("JsonParseException");
    }
}