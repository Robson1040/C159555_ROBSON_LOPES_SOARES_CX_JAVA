package br.gov.caixa.api.investimentos.exception.handler;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;


@Provider
public class WebApplicationExceptionHandler implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        
        System.err.println("WebApplicationException: " + exception.getMessage());
        exception.printStackTrace();

        
        if (exception.getResponse().getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            ErrorResponse errorResponse = ErrorResponse.badRequest(
                    "Dados da requisição inválidos: formato JSON incorreto"
            );
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        }
        
        
        if (exception.getResponse().getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
            ErrorResponse errorResponse = ErrorResponse.unauthorized(
                    "Acesso não autorizado. É necessário fazer login para acessar este recurso."
            );
            
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorResponse)
                    .build();
        }
        
        
        if (exception.getResponse().getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            ErrorResponse errorResponse = ErrorResponse.forbidden(
                    "Acesso negado. Você não possui permissão para acessar este recurso."
            );
            
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorResponse)
                    .build();
        }
        
        
        if (isJsonParsingError(exception)) {
            ErrorResponse errorResponse = ErrorResponse.badRequest(
                    "Formato JSON inválido"
            );
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        }

        
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