package br.gov.caixa.api.investimentos.dto.cliente;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClienteRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateClienteRequestAndAccessFields() {
        String validCPF = "12345678909"; // CPF válido para teste
        ClienteRequest cliente = new ClienteRequest(
                "João Silva",
                validCPF,
                "joaosilva",
                "senha123",
                "USER"
        );

        // Getters
        assertEquals("João Silva", cliente.nome());
        assertEquals(validCPF, cliente.cpf());
        assertEquals("joaosilva", cliente.username());
        assertEquals("senha123", cliente.password());
        assertEquals("USER", cliente.role());

        // toString
        String str = cliente.toString();
        assertTrue(str.contains("João Silva"));
        assertTrue(str.contains(validCPF));
        assertTrue(str.contains("joaosilva"));
        assertTrue(str.contains("senha123"));
        assertTrue(str.contains("USER"));

        // equals & hashCode
        ClienteRequest sameCliente = new ClienteRequest(
                "João Silva",
                validCPF,
                "joaosilva",
                "senha123",
                "USER"
        );
        ClienteRequest differentCliente = new ClienteRequest(
                "Maria",
                "98765432100",
                "maria",
                "senha456",
                "ADMIN"
        );

        assertEquals(cliente, sameCliente);
        assertNotEquals(cliente, differentCliente);
        assertEquals(cliente.hashCode(), sameCliente.hashCode());
        assertNotEquals(cliente.hashCode(), differentCliente.hashCode());
    }

    @Test
    void shouldFailValidationWhenNomeIsBlank() {
        ClienteRequest cliente = new ClienteRequest(
                "",
                "12345678909",
                "username",
                "senha123",
                "USER"
        );

        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Nome é obrigatório")));
    }

    @Test
    void shouldFailValidationWhenCPFIsInvalid() {
        // CPF inválido (sequência ou dígito errado)
        ClienteRequest cliente = new ClienteRequest(
                "João",
                "11111111111",
                "username",
                "senha123",
                "USER"
        );

        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("CPF inválido")));
    }

    @Test
    void shouldFailValidationWhenUsernameIsBlankOrSizeInvalid() {
        ClienteRequest cliente = new ClienteRequest(
                "João",
                "12345678909",
                "ab",
                "senha123",
                "USER"
        );

        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username deve ter entre 3 e 50 caracteres")));
    }

    @Test
    void shouldFailValidationWhenPasswordIsBlankOrTooShort() {
        ClienteRequest cliente = new ClienteRequest(
                "João",
                "12345678909",
                "username",
                "123",
                "USER"
        );

        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password deve ter no mínimo 6 caracteres")));
    }

    @Test
    void shouldFailValidationWhenRoleIsInvalid() {
        ClienteRequest cliente = new ClienteRequest(
                "João",
                "12345678909",
                "username",
                "senha123",
                "INVALID_ROLE"
        );

        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Role deve ser 'USER' ou 'ADMIN'")));
    }
}