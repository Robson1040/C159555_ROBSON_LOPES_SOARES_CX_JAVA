package br.gov.caixa.api.investimentos.dto.cliente;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClienteUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateClienteUpdateRequestAndAccessFields() {
        ClienteUpdateRequest updateRequest = new ClienteUpdateRequest(
                "João Silva",
                "joaosilva",
                "senha123"
        );

        // Testa getters
        assertEquals("João Silva", updateRequest.nome());
        assertEquals("joaosilva", updateRequest.username());
        assertEquals("senha123", updateRequest.password());

        // Testa toString
        String str = updateRequest.toString();
        assertTrue(str.contains("João Silva"));
        assertTrue(str.contains("joaosilva"));
        assertTrue(str.contains("senha123"));

        // Testa equals e hashCode
        ClienteUpdateRequest sameRequest = new ClienteUpdateRequest(
                "João Silva",
                "joaosilva",
                "senha123"
        );
        ClienteUpdateRequest differentRequest = new ClienteUpdateRequest(
                "Maria",
                "maria",
                "senha456"
        );

        assertEquals(updateRequest, sameRequest);
        assertNotEquals(updateRequest, differentRequest);
        assertEquals(updateRequest.hashCode(), sameRequest.hashCode());
        assertNotEquals(updateRequest.hashCode(), differentRequest.hashCode());
    }

    @Test
    void shouldFailValidationWhenNomeTooShort() {
        ClienteUpdateRequest updateRequest = new ClienteUpdateRequest(
                "J",
                "joaosilva",
                "senha123"
        );

        Set<ConstraintViolation<ClienteUpdateRequest>> violations = validator.validate(updateRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Nome deve ter entre 2 e 100 caracteres")));
    }

    @Test
    void shouldFailValidationWhenUsernameTooShort() {
        ClienteUpdateRequest updateRequest = new ClienteUpdateRequest(
                "João Silva",
                "jo",
                "senha123"
        );

        Set<ConstraintViolation<ClienteUpdateRequest>> violations = validator.validate(updateRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username deve ter entre 3 e 50 caracteres")));
    }

    @Test
    void shouldFailValidationWhenPasswordTooShort() {
        ClienteUpdateRequest updateRequest = new ClienteUpdateRequest(
                "João Silva",
                "joaosilva",
                "123"
        );

        Set<ConstraintViolation<ClienteUpdateRequest>> violations = validator.validate(updateRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password deve ter no mínimo 6 caracteres")));
    }

    @Test
    void shouldAllowNullFields() {
        // Como todos os campos são opcionais (sem @NotBlank), null deve passar na validação
        ClienteUpdateRequest updateRequest = new ClienteUpdateRequest(null, null, null);
        Set<ConstraintViolation<ClienteUpdateRequest>> violations = validator.validate(updateRequest);
        assertTrue(violations.isEmpty());
    }
}