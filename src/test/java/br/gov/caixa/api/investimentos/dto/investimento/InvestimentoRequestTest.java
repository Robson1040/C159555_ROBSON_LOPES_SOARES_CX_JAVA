package br.gov.caixa.api.investimentos.dto.investimento;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InvestimentoRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWithExactlyOnePrazo() {
        // Cenários válidos
        InvestimentoRequest byDias = new InvestimentoRequest(1L,1L,BigDecimal.TEN,null,90,null,LocalDate.now());
        InvestimentoRequest byMeses = new InvestimentoRequest(1L,1L,BigDecimal.TEN,12,null,null,LocalDate.now());
        InvestimentoRequest byAnos = new InvestimentoRequest(1L,1L,BigDecimal.TEN,null,null,2,LocalDate.now());

        assertTrue(validator.validate(byDias).isEmpty());
        assertTrue(validator.validate(byMeses).isEmpty());
        assertTrue(validator.validate(byAnos).isEmpty());
    }

    @Test
    void shouldFailValidationWithNoPrazo() {
        InvestimentoRequest request = new InvestimentoRequest(1L,1L,BigDecimal.TEN,null,null,null,LocalDate.now());
        Set<ConstraintViolation<InvestimentoRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Informe pelo menos um prazo")));
    }

    @Test
    void shouldFailValidationWithMultiplePrazos() {
        InvestimentoRequest request1 = new InvestimentoRequest(1L,1L,BigDecimal.TEN,12,30,null,LocalDate.now());
        InvestimentoRequest request2 = new InvestimentoRequest(1L,1L,BigDecimal.TEN,12,null,1,LocalDate.now());
        InvestimentoRequest request3 = new InvestimentoRequest(1L,1L,BigDecimal.TEN,null,30,1,LocalDate.now());

        Set<ConstraintViolation<InvestimentoRequest>> violations1 = validator.validate(request1);
        Set<ConstraintViolation<InvestimentoRequest>> violations2 = validator.validate(request2);
        Set<ConstraintViolation<InvestimentoRequest>> violations3 = validator.validate(request3);

        assertFalse(violations1.isEmpty());
        assertFalse(violations2.isEmpty());
        assertFalse(violations3.isEmpty());

        assertTrue(violations1.stream()
                .anyMatch(v -> v.getMessage().contains("Informe apenas UM tipo de prazo")));
        assertTrue(violations2.stream()
                .anyMatch(v -> v.getMessage().contains("Informe apenas UM tipo de prazo")));
        assertTrue(violations3.stream()
                .anyMatch(v -> v.getMessage().contains("Informe apenas UM tipo de prazo")));
    }

    @Test
    void shouldCalculatePrazoEmDiasAndMesesCorrectly() {
        // Prazo em dias
        InvestimentoRequest byDias = new InvestimentoRequest(1L,1L,BigDecimal.TEN,null,90,null,LocalDate.now());
        assertEquals(90, byDias.getPrazoEmDias());
        assertEquals(3, byDias.getPrazoEmMeses());

        // Prazo em meses
        InvestimentoRequest byMeses = new InvestimentoRequest(1L,1L,BigDecimal.TEN,6,null,null,LocalDate.now());
        assertEquals(180, byMeses.getPrazoEmDias());
        assertEquals(6, byMeses.getPrazoEmMeses());

        // Prazo em anos
        InvestimentoRequest byAnos = new InvestimentoRequest(1L,1L,BigDecimal.TEN,null,null,2,LocalDate.now());
        assertEquals(730, byAnos.getPrazoEmDias());
        assertEquals(24, byAnos.getPrazoEmMeses());
    }
}