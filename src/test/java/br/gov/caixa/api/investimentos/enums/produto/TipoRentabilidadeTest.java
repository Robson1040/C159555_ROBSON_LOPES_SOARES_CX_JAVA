package br.gov.caixa.api.investimentos.enums.produto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TipoRentabilidadeTest {

    @Test
    void shouldContainAllEnumValues() {
        TipoRentabilidade[] expected = {TipoRentabilidade.PRE, TipoRentabilidade.POS};
        assertArrayEquals(expected, TipoRentabilidade.values());
    }

    @Test
    void shouldReturnCorrectEnumByName() {
        assertEquals(TipoRentabilidade.PRE, TipoRentabilidade.valueOf("PRE"));
        assertEquals(TipoRentabilidade.POS, TipoRentabilidade.valueOf("POS"));
    }

    @Test
    void shouldReturnCorrectDescricao() {
        assertEquals("Pré-fixado", TipoRentabilidade.PRE.getDescricao());
        assertEquals("Pós-fixado", TipoRentabilidade.POS.getDescricao());
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> TipoRentabilidade.valueOf("INVALIDO"));
    }
}