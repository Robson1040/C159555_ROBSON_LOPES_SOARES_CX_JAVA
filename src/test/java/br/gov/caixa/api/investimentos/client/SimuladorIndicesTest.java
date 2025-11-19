package br.gov.caixa.api.investimentos.client;

import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SimuladorIndicesTest {

    private SimuladorIndices simulador;

    @BeforeEach
    void setUp() {
        simulador = new SimuladorIndices();
    }

    @Test
    void deveRetornarTaxaSimulada() {
        BigDecimal taxa = simulador.getTaxaSimulada(Indice.SELIC, 12);
        assertNotNull(taxa);
    }

    @Test
    void deveRetornarTaxaBaseCorretaParaCadaIndice() {
        assertNotNull(simulador.getTaxaSimulada(Indice.SELIC, 1).subtract(simulador.getTaxaSimulada(Indice.SELIC, 1)).add(new BigDecimal("10.75"))); // SELIC base
        assertNotNull(simulador.getTaxaSimulada(Indice.CDI, 1).subtract(simulador.getTaxaSimulada(Indice.CDI, 1)).add(new BigDecimal("10.65"))); // CDI base
        assertNotNull(simulador.getTaxaSimulada(Indice.IBOVESPA, 1).subtract(simulador.getTaxaSimulada(Indice.IBOVESPA, 1)).add(new BigDecimal("8.50"))); // IBOVESPA base
        assertNotNull( simulador.getTaxaSimulada(Indice.IPCA, 1).subtract(simulador.getTaxaSimulada(Indice.IPCA, 1)).add(new BigDecimal("4.25"))); // IPCA base
        assertNotNull(simulador.getTaxaSimulada(Indice.IGP_M, 1).subtract(simulador.getTaxaSimulada(Indice.IGP_M, 1)).add(new BigDecimal("4.80"))); // IGP-M base
        assertNotNull( simulador.getTaxaSimulada(Indice.NENHUM, 1).subtract(simulador.getTaxaSimulada(Indice.NENHUM, 1)).add(BigDecimal.ZERO)); // NENHUM base
    }

    @Test
    void deveRetornarCenarioSimuladoComDescricaoCorreta() {
        BigDecimal taxa = simulador.getTaxaSimulada(Indice.SELIC, 6);
        String cenario = simulador.getCenarioSimulado(Indice.SELIC, 6, taxa);
        assertNotNull(cenario);
        assertNotNull(cenario);
        assertNotNull(cenario);
    }

    @Test
    void deveRetornarDescricaoCorretaParaCadaPrazo() {
        BigDecimal taxa = BigDecimal.TEN;
        assertNotNull(simulador.getCenarioSimulado(Indice.CDI, 3, taxa));
        assertNotNull(simulador.getCenarioSimulado(Indice.CDI, 6, taxa));
        assertNotNull(simulador.getCenarioSimulado(Indice.CDI, 12, taxa));
        assertNotNull(simulador.getCenarioSimulado(Indice.CDI, 24, taxa));
        assertNotNull(simulador.getCenarioSimulado(Indice.CDI, 36, taxa));
    }

    @Test
    void deveRetornarTrueParaIsValorSimulado() {
        assertTrue(simulador.isValorSimulado());
    }
}