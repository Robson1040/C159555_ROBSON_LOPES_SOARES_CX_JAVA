package br.gov.caixa.api.investimentos.enums.simulacao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TipoRendaTest {

    @Test
    void shouldContainAllEnumValues() {
        TipoRenda[] expected = {TipoRenda.RENDA_FIXA, TipoRenda.RENDA_VARIAVEL};
        assertArrayEquals(expected, TipoRenda.values());
    }

    @Test
    void shouldReturnCorrectEnumByName() {
        assertEquals(TipoRenda.RENDA_FIXA, TipoRenda.valueOf("RENDA_FIXA"));
        assertEquals(TipoRenda.RENDA_VARIAVEL, TipoRenda.valueOf("RENDA_VARIAVEL"));
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> TipoRenda.valueOf("INVALIDO"));
    }
}