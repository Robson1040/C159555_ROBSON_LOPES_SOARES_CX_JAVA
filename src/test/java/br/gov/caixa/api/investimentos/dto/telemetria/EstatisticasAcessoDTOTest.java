package br.gov.caixa.api.investimentos.dto.telemetria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstatisticasAcessoDTOTest {
    @Test
    void testRecordFields() {
        long totalAcessos = 100L;
        long acessosComSucesso = 80L;
        long acessosComErro = 20L;
        double taxaSucesso = 80.0;
        double taxaErro = 20.0;

        EstatisticasAcessoDTO dto = new EstatisticasAcessoDTO(
            totalAcessos, acessosComSucesso, acessosComErro, taxaSucesso, taxaErro
        );

        assertEquals(totalAcessos, dto.totalAcessos());
        assertEquals(acessosComSucesso, dto.acessosComSucesso());
        assertEquals(acessosComErro, dto.acessosComErro());
        assertEquals(taxaSucesso, dto.taxaSucesso());
        assertEquals(taxaErro, dto.taxaErro());
    }

    @Test
    void testEqualsAndHashCode() {
        EstatisticasAcessoDTO dto1 = new EstatisticasAcessoDTO(100L, 80L, 20L, 80.0, 20.0);
        EstatisticasAcessoDTO dto2 = new EstatisticasAcessoDTO(100L, 80L, 20L, 80.0, 20.0);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        EstatisticasAcessoDTO dto = new EstatisticasAcessoDTO(100L, 80L, 20L, 80.0, 20.0);
        String str = dto.toString();
        assertTrue(str.contains("EstatisticasAcessoDTO"));
        assertTrue(str.contains("totalAcessos=100"));
        assertTrue(str.contains("acessosComSucesso=80"));
        assertTrue(str.contains("acessosComErro=20"));
        assertTrue(str.contains("taxaSucesso=80.0"));
        assertTrue(str.contains("taxaErro=20.0"));
    }
}
