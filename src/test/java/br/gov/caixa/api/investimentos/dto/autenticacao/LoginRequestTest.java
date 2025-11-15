package br.gov.caixa.api.investimentos.dto.autenticacao;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateLoginRequestAndAccessFields() {
        LoginRequest request = new LoginRequest("user123", "pass123");

        // Testa getters
        assertEquals("user123", request.username());
        assertEquals("pass123", request.password());

        // Testa toString
        String str = request.toString();
        assertTrue(str.contains("user123"));
        assertTrue(str.contains("pass123"));

        // Testa equals e hashCode
        LoginRequest sameRequest = new LoginRequest("user123", "pass123");
        LoginRequest differentRequest = new LoginRequest("user456", "pass456");

        assertEquals(request, sameRequest);
        assertNotEquals(request, differentRequest);
        assertEquals(request.hashCode(), sameRequest.hashCode());
        assertNotEquals(request.hashCode(), differentRequest.hashCode());
    }

    @Test
    void shouldFailValidationWhenUsernameIsBlank() {
        LoginRequest request = new LoginRequest("", "pass123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username é obrigatório")));
    }

    @Test
    void shouldFailValidationWhenPasswordIsBlank() {
        LoginRequest request = new LoginRequest("user123", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password é obrigatório")));
    }

    @Test
    void shouldFailValidationWhenBothFieldsAreBlank() {
        LoginRequest request = new LoginRequest("", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }
}