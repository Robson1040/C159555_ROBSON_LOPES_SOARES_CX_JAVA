package br.gov.caixa.api.investimentos.validation;

import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;

import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidPrazoValidatorTest {

    private ValidPrazoValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setup() {
        validator = new ValidPrazoValidator();
        context = mock(ConstraintValidatorContext.class);

        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
        when(builder.addPropertyNode(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
    }

    @Test
    void testNenhumPrazoInformado() {
        SimulacaoRequest req = criarSimulacaoRequest(null, null, null);
        boolean result = validator.isValid(req, context);
        assertFalse(result, "Deve ser inválido quando nenhum prazo é informado");
    }

    @Test
    void testMaisDeUmPrazoInformado() {
        SimulacaoRequest req = criarSimulacaoRequest(12, 30, null);
        boolean result = validator.isValid(req, context);
        assertFalse(result, "Deve ser inválido quando mais de um prazo é informado");
    }

    @Test
    void testApenasPrazoMeses() {
        SimulacaoRequest req = criarSimulacaoRequest(12, null, null);
        boolean result = validator.isValid(req, context);
        assertTrue(result, "Deve ser válido quando apenas o prazo em meses é informado");
    }

    @Test
    void testApenasPrazoDias() {
        SimulacaoRequest req = criarSimulacaoRequest(null, 30, null);
        boolean result = validator.isValid(req, context);
        assertTrue(result, "Deve ser válido quando apenas o prazo em dias é informado");
    }

    @Test
    void testApenasPrazoAnos() {
        SimulacaoRequest req = criarSimulacaoRequest(null, null, 2);
        boolean result = validator.isValid(req, context);
        assertTrue(result, "Deve ser válido quando apenas o prazo em anos é informado");
    }

    /**
     * Método auxiliar para criar SimulacaoRequest preenchido
     */
    private SimulacaoRequest criarSimulacaoRequest(Integer prazoMeses, Integer prazoDias, Integer prazoAnos) {
        return new SimulacaoRequest(
                1L,                          // clienteId
                1L,                          // produtoId
                new BigDecimal("1000.00"),   // valor
                prazoMeses,
                prazoDias,
                prazoAnos,
                TipoProduto.CDB,              // tipoProduto
                "CDB Teste",                  // nome
                TipoRentabilidade.PRE,        // tipoRentabilidade
                Indice.NENHUM,                // indice
                0,                            // liquidez
                true                          // fgc
        );
    }
}