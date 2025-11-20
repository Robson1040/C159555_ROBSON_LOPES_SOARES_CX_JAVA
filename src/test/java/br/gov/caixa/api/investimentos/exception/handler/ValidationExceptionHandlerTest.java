package br.gov.caixa.api.investimentos.exception.handler;

import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationExceptionHandlerTest {

    private ValidationExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ValidationExceptionHandler();
    }

    @Test
    void toResponse_singleViolation() {
        // Mock do ConstraintViolation e Path
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("simularInvestimento.request.valor");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("deve ser maior que zero");
        when(violation.getInvalidValue()).thenReturn(-100);

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException exception = new ConstraintViolationException(violations);

        Response response = handler.toResponse(exception);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertNotNull(entity);
        assertEquals(400, entity.status());
        assertEquals("Dados inválidos fornecidos", entity.message());
        assertEquals(1, entity.errors().size());
        assertTrue(entity.errors().get(0).contains("valor"));
        assertTrue(entity.errors().get(0).contains("-100"));
        assertTrue(entity.errors().get(0).contains("maior que zero"));
    }

    @Test
    void toResponse_multipleViolations() {
        // Primeiro violation
        ConstraintViolation<Object> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("campo1");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("não pode ser nulo");
        when(violation1.getInvalidValue()).thenReturn(null);

        // Segundo violation
        ConstraintViolation<Object> violation2 = mock(ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("obj.campo2");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("tamanho inválido");
        when(violation2.getInvalidValue()).thenReturn("abc");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation1);
        violations.add(violation2);

        ConstraintViolationException exception = new ConstraintViolationException(violations);

        Response response = handler.toResponse(exception);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertNotNull(entity);
        assertEquals(400, entity.status());
        assertEquals("Dados inválidos fornecidos", entity.message());
        assertEquals(2, entity.errors().size());
        assertTrue(entity.errors().stream().anyMatch(e -> e.contains("campo1") && e.contains("nulo")));
        assertTrue(entity.errors().stream().anyMatch(e -> e.contains("campo2") && e.contains("abc")));
    }
}