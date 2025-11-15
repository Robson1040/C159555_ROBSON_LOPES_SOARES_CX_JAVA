package br.gov.caixa.api.investimentos.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CPFValidatorTest {

    private CPFValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new CPFValidator();
        context = mock(ConstraintValidatorContext.class); // não precisamos de stubs reais
    }

    @Test
    void testValidCPF() {
        // CPFs válidos
        assertTrue(validator.isValid("12345678909", context)); // exemplo válido
        assertTrue(validator.isValid("11144477735", context)); // outro exemplo válido
    }

    @Test
    void testNullOrEmptyCPF() {
        assertFalse(validator.isValid(null, context));
        assertFalse(validator.isValid("", context));
    }

    @Test
    void testCPFWithWrongLength() {
        assertFalse(validator.isValid("1234567890", context));  // 10 dígitos
        assertFalse(validator.isValid("123456789012", context)); // 12 dígitos
    }

    @Test
    void testCPFWithAllSameDigits() {
        assertFalse(validator.isValid("00000000000", context));
        assertFalse(validator.isValid("11111111111", context));
        assertFalse(validator.isValid("99999999999", context));
    }

    @Test
    void testCPFWithInvalidCheckDigits() {
        // Digitos verificadores incorretos
        assertFalse(validator.isValid("12345678901", context));
        assertFalse(validator.isValid("11144477734", context));
    }
}
