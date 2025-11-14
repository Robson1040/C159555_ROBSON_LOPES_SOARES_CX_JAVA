package org.example.service.telemetria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetricasManagerTest {
    
    private MetricasManager metricasManager;
    
    @BeforeEach
    void setUp() {
        metricasManager = new MetricasManager();
    }
    
    @Test
    void testIncrementarContador() {
        // Testa incremento de contador
        metricasManager.incrementarContador("produtos");
        metricasManager.incrementarContador("produtos");
        metricasManager.incrementarContador("clientes");
        
        assertEquals(2L, metricasManager.obterContadorExecucoes("produtos"));
        assertEquals(1L, metricasManager.obterContadorExecucoes("clientes"));
        assertEquals(0L, metricasManager.obterContadorExecucoes("inexistente"));
    }
    
    @Test
    void testRegistrarTempoResposta() {
        // Testa registro de tempo de resposta
        metricasManager.registrarTempoResposta("produtos", 100);
        metricasManager.registrarTempoResposta("produtos", 200);
        metricasManager.registrarTempoResposta("clientes", 150);
        
        assertEquals(150.0, metricasManager.obterTempoMedioResposta("produtos"), 0.1);
        assertEquals(150.0, metricasManager.obterTempoMedioResposta("clientes"), 0.1);
        assertEquals(0.0, metricasManager.obterTempoMedioResposta("inexistente"), 0.1);
    }
    
    @Test
    void testEndpointsComMetricas() {
        // Testa listagem de endpoints com métricas
        metricasManager.incrementarContador("produtos");
        metricasManager.incrementarContador("clientes");
        
        var endpoints = metricasManager.obterEndpointsComMetricas();
        
        assertEquals(2, endpoints.size());
        assertTrue(endpoints.contains("produtos"));
        assertTrue(endpoints.contains("clientes"));
    }
    
    @Test
    void testLimparMetricas() {
        // Adiciona algumas métricas
        metricasManager.incrementarContador("produtos");
        metricasManager.registrarTempoResposta("produtos", 100);
        
        // Verifica se métricas existem
        assertEquals(1L, metricasManager.obterContadorExecucoes("produtos"));
        assertEquals(100.0, metricasManager.obterTempoMedioResposta("produtos"), 0.1);
        
        // Limpa métricas
        metricasManager.limparMetricas();
        
        // Verifica se métricas foram limpas
        assertEquals(0L, metricasManager.obterContadorExecucoes("produtos"));
        assertEquals(0.0, metricasManager.obterTempoMedioResposta("produtos"), 0.1);
        assertTrue(metricasManager.obterEndpointsComMetricas().isEmpty());
    }
    
    @Test
    void testNullSafety() {
        // Testa comportamento com valores null
        metricasManager.incrementarContador(null);
        metricasManager.incrementarContador("");
        metricasManager.registrarTempoResposta(null, 100);
        metricasManager.registrarTempoResposta("", 100);
        
        // Não deve ter adicionado métricas
        assertTrue(metricasManager.obterEndpointsComMetricas().isEmpty());
    }
}