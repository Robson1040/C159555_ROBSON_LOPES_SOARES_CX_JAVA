package br.gov.caixa.api.investimentos.enums.simulacao;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IndiceTest {

    @Test
    void shouldContainAllEnumValues() {
        Indice[] expected = {Indice.SELIC, Indice.CDI, Indice.IBOVESPA, Indice.IPCA, Indice.IGP_M, Indice.NENHUM};
        assertArrayEquals(expected, Indice.values());
    }

    @Test
    void shouldReturnCorrectEnumByName() {
        for (Indice indice : Indice.values()) {
            assertEquals(indice, Indice.valueOf(indice.name()));
        }
    }

    @Test
    void shouldReturnCorrectDescricao() {
        assertEquals("Selic", Indice.SELIC.getDescricao());
        assertEquals("CDI", Indice.CDI.getDescricao());
        assertEquals("Ibovespa", Indice.IBOVESPA.getDescricao());
        assertEquals("IPCA", Indice.IPCA.getDescricao());
        assertEquals("IGP-M", Indice.IGP_M.getDescricao());
        assertEquals("Nenhum", Indice.NENHUM.getDescricao());
    }

    @Test
    void shouldReturnCorrectTaxaAnualSimulada() {
        assertEquals(new BigDecimal("10.75"), Indice.SELIC.getTaxaAnualSimulada(12));
        assertEquals(new BigDecimal("10.65"), Indice.CDI.getTaxaAnualSimulada(12));
        assertEquals(new BigDecimal("8.50"), Indice.IBOVESPA.getTaxaAnualSimulada(12));
        assertEquals(new BigDecimal("4.25"), Indice.IPCA.getTaxaAnualSimulada(12));
        assertEquals(new BigDecimal("4.80"), Indice.IGP_M.getTaxaAnualSimulada(12));
        assertEquals(BigDecimal.ZERO, Indice.NENHUM.getTaxaAnualSimulada(12));
    }

    @Test
    void shouldReturnCorrectTaxaDecimal() {
        assertEquals(new BigDecimal("0.1075"), Indice.SELIC.getTaxaDecimal(12));
        assertEquals(new BigDecimal("0.1065"), Indice.CDI.getTaxaDecimal(12));
        assertEquals(new BigDecimal("0.0850"), Indice.IBOVESPA.getTaxaDecimal(12));
        assertEquals(new BigDecimal("0.0425"), Indice.IPCA.getTaxaDecimal(12));
        assertEquals(new BigDecimal("0.0480"), Indice.IGP_M.getTaxaDecimal(12));
        assertEquals(BigDecimal.ZERO, Indice.NENHUM.getTaxaDecimal(12));
    }

    @Test
    void deprecatedMethodsShouldReturnSameValues() {
        assertEquals(Indice.SELIC.getTaxaAnualSimulada(12), Indice.SELIC.getTaxaAnualSimulada());
        assertEquals(Indice.SELIC.getTaxaDecimal(12), Indice.SELIC.getTaxaDecimal());
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> Indice.valueOf("INVALIDO"));
    }
}