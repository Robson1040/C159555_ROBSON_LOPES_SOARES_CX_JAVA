package br.gov.caixa.api.investimentos.exception.handler;

import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidEnumExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    @Override
    public Response toResponse(InvalidFormatException exception) {
        StringBuilder body = new StringBuilder();
        body.append("Valor inválido para o ");
        body.append("campo " + exception.getPath().get(0).getFieldName());
        body.append(". valorRecebido: " + exception.getValue());

        ErrorResponse errorResponse = ErrorResponse.badRequest(
                body.toString()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}