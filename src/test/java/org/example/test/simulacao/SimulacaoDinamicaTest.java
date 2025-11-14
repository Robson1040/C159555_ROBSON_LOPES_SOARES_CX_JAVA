package org.example.test.simulacao;

import org.example.model.Indice;
import org.example.model.TipoProduto;
import org.example.service.simulacao.SimuladorIndices;
import org.example.service.simulacao.SimuladorMercado;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa o sistema de simulação dinâmica implementado
 * Este é um teste de documentação das funcionalidades implementadas
 */
public class SimulacaoDinamicaTest {

    @Test
    public void testSimuladorIndicesVariaPorPeriodo() {
        SimuladorIndices simulador = new SimuladorIndices();
        
        // Testa que diferentes períodos geram diferentes valores
        BigDecimal taxa6Meses = simulador.getTaxaSimulada(Indice.SELIC, 6);
        BigDecimal taxa12Meses = simulador.getTaxaSimulada(Indice.SELIC, 12);
        BigDecimal taxa24Meses = simulador.getTaxaSimulada(Indice.SELIC, 24);
        
        // Verifica que os valores são diferentes (simulação dinâmica)
        assertNotEquals(taxa6Meses, taxa12Meses);
        assertNotEquals(taxa12Meses, taxa24Meses);
        
        // Verifica que são valores razoáveis (positivos)
        assertTrue(taxa6Meses.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(taxa12Meses.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(taxa24Meses.compareTo(BigDecimal.ZERO) > 0);
        
        System.out.println("Taxa SELIC 6 meses: " + taxa6Meses + "%");
        System.out.println("Taxa SELIC 12 meses: " + taxa12Meses + "%");
        System.out.println("Taxa SELIC 24 meses: " + taxa24Meses + "%");
    }
    
    @Test
    public void testSimuladorMercadoGeraCenarios() {
        SimuladorMercado simulador = new SimuladorMercado();
        
        // Testa cenários para diferentes períodos e produtos
        SimuladorMercado.CenarioMercado cenario6Meses = 
            simulador.gerarCenario(TipoProduto.CDB, 6);
        SimuladorMercado.CenarioMercado cenario24Meses = 
            simulador.gerarCenario(TipoProduto.FUNDO, 24);
        
        assertNotNull(cenario6Meses);
        assertNotNull(cenario24Meses);
        assertNotNull(cenario6Meses.getDescricao());
        assertNotNull(cenario24Meses.getDescricao());
        assertTrue(cenario6Meses.isSimulado());
        assertTrue(cenario24Meses.isSimulado());
        
        System.out.println("Cenário 6 meses CDB: " + cenario6Meses.getDescricao());
        System.out.println("Cenário 24 meses Fundo: " + cenario24Meses.getDescricao());
        System.out.println("Multiplicador de risco 6m: " + cenario6Meses.getMultiplicadorRisco());
        System.out.println("Multiplicador de risco 24m: " + cenario24Meses.getMultiplicadorRisco());
    }
    
    @Test
    public void testVariabilidadeTemporalIndices() {
        SimuladorIndices simulador = new SimuladorIndices();
        
        // Testa que diferentes índices têm comportamentos diferentes
        BigDecimal selicSimulada = simulador.getTaxaSimulada(Indice.SELIC, 12);
        BigDecimal cdiSimulado = simulador.getTaxaSimulada(Indice.CDI, 12);
        BigDecimal ibovespaSimulado = simulador.getTaxaSimulada(Indice.IBOVESPA, 12);
        
        // CDI deveria estar próximo da SELIC
        assertTrue(Math.abs(cdiSimulado.doubleValue() - selicSimulada.doubleValue()) < 2.0);
        
        // IBOVESPA pode variar mais
        assertNotNull(ibovespaSimulado);
        
        System.out.println("SELIC simulada 12m: " + selicSimulada + "%");
        System.out.println("CDI simulado 12m: " + cdiSimulado + "%");
        System.out.println("IBOVESPA simulado 12m: " + ibovespaSimulado + "%");
    }
    
    @Test
    public void testCenarioDescricaoContemPeriodo() {
        SimuladorIndices simulador = new SimuladorIndices();
        
        BigDecimal taxa6 = simulador.getTaxaSimulada(Indice.CDI, 6);
        String cenario = simulador.getCenarioSimulado(Indice.CDI, 6, taxa6);
        assertTrue(cenario.contains("6"));
        
        BigDecimal taxa12 = simulador.getTaxaSimulada(Indice.CDI, 12);
        String cenario12 = simulador.getCenarioSimulado(Indice.CDI, 12, taxa12);
        assertTrue(cenario12.contains("12"));
        
        System.out.println("Cenário CDI 6 meses: " + cenario);
        System.out.println("Cenário CDI 12 meses: " + cenario12);
    }
}