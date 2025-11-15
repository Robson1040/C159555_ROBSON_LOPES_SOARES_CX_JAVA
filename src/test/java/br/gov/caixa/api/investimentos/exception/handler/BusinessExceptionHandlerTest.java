package br.gov.caixa.api.investimentos.exception.handler;

import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionHandlerTest {

    private BusinessExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new BusinessExceptionHandler();
    }

    @Test
    void toResponse_validationError() {
        RuntimeException ex = new RuntimeException("Dados inválidos fornecidos");

        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertNotNull(entity);
        assertEquals(400, entity.status());
        assertTrue(entity.message().toLowerCase().contains("validação") ||
                entity.message().toLowerCase().contains("dados inválidos"));
    }

    @Test
    void toResponse_notFoundError() {
        RuntimeException ex = new RuntimeException("Produto não encontrado");

        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertNotNull(entity);
        assertEquals(404, entity.status());
        assertTrue(entity.message().toLowerCase().contains("produto não encontrado"));
    }

    @Test
    void toResponse_genericError() {
        RuntimeException ex = new RuntimeException("Erro inesperado no sistema");

        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertNotNull(entity);
        assertEquals(500, entity.status());
        assertTrue(entity.message().toLowerCase().contains("erro interno do servidor"));
        assertTrue(entity.message().contains("Erro inesperado no sistema"));
    }
}