package br.gov.caixa.api.investimentos.enums.produto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PeriodoRentabilidadeTest {

    @Test
    void shouldContainAllEnumValues() {
        PeriodoRentabilidade[] expected = {
                PeriodoRentabilidade.AO_DIA,
                PeriodoRentabilidade.AO_MES,
                PeriodoRentabilidade.AO_ANO,
                PeriodoRentabilidade.PERIODO_TOTAL
        };
        assertArrayEquals(expected, PeriodoRentabilidade.values());
    }

    @Test
    void shouldReturnCorrectEnumByName() {
        assertEquals(PeriodoRentabilidade.AO_DIA, PeriodoRentabilidade.valueOf("AO_DIA"));
        assertEquals(PeriodoRentabilidade.AO_MES, PeriodoRentabilidade.valueOf("AO_MES"));
        assertEquals(PeriodoRentabilidade.AO_ANO, PeriodoRentabilidade.valueOf("AO_ANO"));
        assertEquals(PeriodoRentabilidade.PERIODO_TOTAL, PeriodoRentabilidade.valueOf("PERIODO_TOTAL"));
    }

    @Test
    void shouldReturnCorrectDescricao() {
        assertEquals("Ao Dia", PeriodoRentabilidade.AO_DIA.getDescricao());
        assertEquals("Ao Mês", PeriodoRentabilidade.AO_MES.getDescricao());
        assertEquals("Ao Ano", PeriodoRentabilidade.AO_ANO.getDescricao());
        assertEquals("Período Total", PeriodoRentabilidade.PERIODO_TOTAL.getDescricao());
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> PeriodoRentabilidade.valueOf("INVALIDO"));
    }
}