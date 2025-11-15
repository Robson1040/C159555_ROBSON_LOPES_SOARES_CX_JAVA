package br.gov.caixa.api.investimentos.enums.produto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NivelRiscoTest {

    @Test
    void shouldContainAllEnumValues() {
        NivelRisco[] expected = {NivelRisco.BAIXO, NivelRisco.MEDIO, NivelRisco.ALTO};
        assertArrayEquals(expected, NivelRisco.values());
    }

    @Test
    void shouldReturnCorrectEnumByName() {
        assertEquals(NivelRisco.BAIXO, NivelRisco.valueOf("BAIXO"));
        assertEquals(NivelRisco.MEDIO, NivelRisco.valueOf("MEDIO"));
        assertEquals(NivelRisco.ALTO, NivelRisco.valueOf("ALTO"));
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> NivelRisco.valueOf("INDETERMINADO"));
    }
}
