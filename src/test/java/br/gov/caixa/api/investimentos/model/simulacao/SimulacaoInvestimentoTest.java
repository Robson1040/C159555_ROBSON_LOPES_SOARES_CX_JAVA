package br.gov.caixa.api.investimentos.model.simulacao;

import br.gov.caixa.api.investimentos.dto.simulacao.ResultadoSimulacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SimulacaoInvestimentoTest {

    private SimulacaoInvestimento simulacao;
    private ResultadoSimulacao resultado;

    @BeforeEach
    void setUp() {
        resultado = new ResultadoSimulacao(
                BigDecimal.valueOf(1200.0),
                BigDecimal.valueOf(0.12),
                0,
                1,
                null,
                BigDecimal.valueOf(0.05),
                BigDecimal.valueOf(200.0),
                true,
                "cenário teste"
        );

        simulacao = new SimulacaoInvestimento(
                1L,
                100L,
                "Produto Teste",
                BigDecimal.valueOf(1000),
                resultado.valorFinal(),
                12,
                0,
                1,
                resultado.rentabilidadeEfetiva(),
                resultado.rendimento(),
                resultado.valorSimulado(),
                resultado.cenarioSimulacao()
        );
    }

    @Test
    void testConstrutores() {
        SimulacaoInvestimento s1 = new SimulacaoInvestimento();
        assertNotNull(s1.getDataSimulacao());

        SimulacaoInvestimento s2 = new SimulacaoInvestimento(1L, 100L, "Prod", BigDecimal.ONE, BigDecimal.TEN, 1, 2, 3);
        assertEquals(1L, s2.getClienteId());
        assertEquals(100L, s2.getProdutoId());
        assertEquals("Prod", s2.getProduto());
        assertEquals(BigDecimal.ONE, s2.getValorInvestido());
        assertEquals(BigDecimal.TEN, s2.getValorFinal());
        assertEquals(1, s2.getPrazoMeses());
        assertEquals(2, s2.getPrazoDias());
        assertEquals(3, s2.getPrazoAnos());
    }

    @Test
    void testFromSimulacao() {
        SimulacaoInvestimento s = SimulacaoInvestimento.fromSimulacao(
                1L,
                100L,
                "Produto Teste",
                BigDecimal.valueOf(1000),
                resultado
        );

        assertEquals(1L, s.getClienteId());
        assertEquals(100L, s.getProdutoId());
        assertEquals("Produto Teste", s.getProduto());
        assertEquals(BigDecimal.valueOf(1000), s.getValorInvestido());
        assertEquals(resultado.valorFinal(), s.getValorFinal());
        assertEquals(resultado.rentabilidadeEfetiva(), s.getRentabilidadeEfetiva());
        assertEquals(resultado.rendimento(), s.getRendimento());
        assertEquals(resultado.valorSimulado(), s.getValorSimulado());
        assertEquals(resultado.cenarioSimulacao(), s.getCenarioSimulacao());
    }

    @Test
    void testSettersEGetters() {
        simulacao.setClienteId(2L);
        simulacao.setProdutoId(200L);
        simulacao.setProduto("Novo Produto");
        simulacao.setValorInvestido(BigDecimal.valueOf(500));
        simulacao.setValorFinal(BigDecimal.valueOf(1500));
        simulacao.setPrazoMeses(24);
        simulacao.setPrazoDias(10);
        simulacao.setPrazoAnos(2);
        simulacao.setRentabilidadeEfetiva(BigDecimal.valueOf(0.1));
        simulacao.setRendimento(BigDecimal.valueOf(300));
        simulacao.setValorSimulado(false);
        simulacao.setCenarioSimulacao("Novo cenário");
        LocalDateTime now = LocalDateTime.now();
        simulacao.setDataSimulacao(now);

        assertEquals(2L, simulacao.getClienteId());
        assertEquals(200L, simulacao.getProdutoId());
        assertEquals("Novo Produto", simulacao.getProduto());
        assertEquals(BigDecimal.valueOf(500), simulacao.getValorInvestido());
        assertEquals(BigDecimal.valueOf(1500), simulacao.getValorFinal());
        assertEquals(24, simulacao.getPrazoMeses());
        assertEquals(10, simulacao.getPrazoDias());
        assertEquals(2, simulacao.getPrazoAnos());
        assertEquals(BigDecimal.valueOf(0.1), simulacao.getRentabilidadeEfetiva());
        assertEquals(BigDecimal.valueOf(300), simulacao.getRendimento());
        assertFalse(simulacao.getValorSimulado());
        assertEquals("Novo cenário", simulacao.getCenarioSimulacao());
        assertEquals(now, simulacao.getDataSimulacao());
    }

    @Test
    void testToString() {
        String str = simulacao.toString();
        assertTrue(str.contains("clienteId=1"));
        assertTrue(str.contains("Produto Teste"));
        assertTrue(str.contains("valorInvestido=1000"));
        assertTrue(str.contains("valorFinal=1200"));
    }
}
