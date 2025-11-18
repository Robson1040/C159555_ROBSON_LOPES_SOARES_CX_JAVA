package br.gov.caixa.api.investimentos.exception.handler;

import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InvalidEnumExceptionMapperTest {

    @Test
    void deveRetornarBadRequestComMensagemCorreta() {
        // Arrange: simula um InvalidFormatException com campo "tipo" e valor "TESTE"
        JsonMappingException.Reference reference = new JsonMappingException.Reference(null, "tipo");
        InvalidFormatException exception = InvalidFormatException.from(null, "Valor inválido", "TESTE", TipoProduto.class);
        exception.prependPath(reference);

        InvalidEnumExceptionMapper mapper = new InvalidEnumExceptionMapper();

        // Act: chama o método toResponse
        Response response = mapper.toResponse(exception);

        // Assert: verifica status e conteúdo
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        assertTrue(response.getEntity() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();

        assertTrue(errorResponse.message().contains("Valor inválido"));
        assertTrue(errorResponse.message().contains("tipo"));
        assertTrue(errorResponse.message().contains("TESTE"));
    }

    // Enum fictício para simular valores permitidos
    enum TipoProduto {
        A, B, C
    }
}
