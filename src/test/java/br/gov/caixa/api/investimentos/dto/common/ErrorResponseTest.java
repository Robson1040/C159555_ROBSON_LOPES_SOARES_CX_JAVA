package br.gov.caixa.api.investimentos.dto.common;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseUsingMainConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<String> errors = List.of("e1", "e2");

        ErrorResponse response = new ErrorResponse(
                "Mensagem",
                now,
                400,
                "/api/teste",
                errors
        );

        assertEquals("Mensagem", response.message());
        assertEquals(now, response.timestamp());
        assertEquals(400, response.status());
        assertEquals("/api/teste", response.path());
        assertEquals(errors, response.errors());

        // toString
        assertTrue(response.toString().contains("Mensagem"));

        // equals e hashCode
        ErrorResponse same = new ErrorResponse("Mensagem", now, 400, "/api/teste", errors);
        assertEquals(response, same);
        assertEquals(response.hashCode(), same.hashCode());
    }

    @Test
    void shouldCreateErrorResponseSimpleConstructor() {
        ErrorResponse response = new ErrorResponse("Erro simples");

        assertEquals("Erro simples", response.message());
        assertNotNull(response.timestamp());
        assertNull(response.status());
        assertNull(response.path());
        assertNull(response.errors());
    }

    @Test
    void shouldCreateErrorResponseConstructorWithStatus() {
        ErrorResponse response = new ErrorResponse("Erro", 500);

        assertEquals("Erro", response.message());
        assertNotNull(response.timestamp());
        assertEquals(500, response.status());
        assertNull(response.path());
        assertNull(response.errors());
    }

    @Test
    void shouldCreateErrorResponseConstructorWithStatusAndPath() {
        ErrorResponse response = new ErrorResponse("Erro", 404, "/api/x");

        assertEquals("Erro", response.message());
        assertNotNull(response.timestamp());
        assertEquals(404, response.status());
        assertEquals("/api/x", response.path());
        assertNull(response.errors());
    }

    @Test
    void shouldCreateBadRequest() {
        ErrorResponse r = ErrorResponse.badRequest("Erro");

        assertEquals("Erro", r.message());
        assertEquals(400, r.status());
        assertNull(r.path());
        assertNull(r.errors());
    }

    @Test
    void shouldCreateBadRequestWithPath() {
        ErrorResponse r = ErrorResponse.badRequest("Erro", "/api");

        assertEquals("Erro", r.message());
        assertEquals(400, r.status());
        assertEquals("/api", r.path());
        assertNull(r.errors());
    }

    @Test
    void shouldCreateBadRequestWithPathAndErrors() {
        List<String> errs = List.of("e1", "e2");

        ErrorResponse r = ErrorResponse.badRequest("Erro", "/api", errs);

        assertEquals("Erro", r.message());
        assertEquals(400, r.status());
        assertEquals("/api", r.path());
        assertEquals(errs, r.errors());
    }

    @Test
    void shouldCreateNotFound() {
        ErrorResponse r = ErrorResponse.notFound("Não encontrado");

        assertEquals("Não encontrado", r.message());
        assertEquals(404, r.status());
        assertNull(r.path());
    }

    @Test
    void shouldCreateInternalError() {
        ErrorResponse r = ErrorResponse.internalError("Erro interno");

        assertEquals("Erro interno", r.message());
        assertEquals(500, r.status());
    }

    @Test
    void shouldCreateValidationError() {
        List<String> e = List.of("campo x inválido");

        ErrorResponse r = ErrorResponse.validationError("Erro", e);

        assertEquals("Erro", r.message());
        assertEquals(400, r.status());
        assertEquals(e, r.errors());
    }

    @Test
    void shouldCreateUnauthorized() {
        ErrorResponse r = ErrorResponse.unauthorized("Não autorizado");

        assertEquals("Não autorizado", r.message());
        assertEquals(401, r.status());
    }

    @Test
    void shouldCreateForbidden() {
        ErrorResponse r = ErrorResponse.forbidden("Proibido");

        assertEquals("Proibido", r.message());
        assertEquals(403, r.status());
    }
}
