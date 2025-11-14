package org.example.service.telemetria;

import org.example.dto.telemetria.TelemetriaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TelemetriaServiceTest {
    
    @Mock
    private MetricasManager metricasManager;
    
    @InjectMocks
    private TelemetriaService telemetriaService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testObterTelemetriaComDadosReais() {
        // Configura o mock para retornar dados de métricas
        when(metricasManager.obterEndpointsComMetricas())
            .thenReturn(Set.of("produtos", "clientes", "simular-investimento"));
        
        when(metricasManager.obterContadorExecucoes("produtos")).thenReturn(5L);
        when(metricasManager.obterTempoMedioResposta("produtos")).thenReturn(120.5);
        
        when(metricasManager.obterContadorExecucoes("clientes")).thenReturn(3L);
        when(metricasManager.obterTempoMedioResposta("clientes")).thenReturn(89.2);
        
        when(metricasManager.obterContadorExecucoes("simular-investimento")).thenReturn(2L);
        when(metricasManager.obterTempoMedioResposta("simular-investimento")).thenReturn(250.8);
        
        // Executa o método
        TelemetriaResponse response = telemetriaService.obterTelemetria();
        
        // Verifica os resultados
        assertNotNull(response);
        assertNotNull(response.getServicos());
        assertEquals(3, response.getServicos().size());
        
        // Verifica se contém os serviços esperados
        var servicoNomes = response.getServicos().stream()
            .map(s -> s.getNome())
            .toList();
        
        assertTrue(servicoNomes.contains("produtos"));
        assertTrue(servicoNomes.contains("clientes"));
        assertTrue(servicoNomes.contains("simular-investimento"));
        
        // Verifica período
        assertNotNull(response.getPeriodo());
        assertNotNull(response.getPeriodo().getInicio());
        assertNotNull(response.getPeriodo().getFim());
    }
    
    @Test
    void testObterTelemetriaSemDadosReais() {
        // Configura o mock para não retornar métricas
        when(metricasManager.obterEndpointsComMetricas()).thenReturn(Set.of());
        
        // Executa o método
        TelemetriaResponse response = telemetriaService.obterTelemetria();
        
        // Verifica que retorna dados de exemplo
        assertNotNull(response);
        assertNotNull(response.getServicos());
        assertEquals(3, response.getServicos().size());
        
        // Verifica se são os dados de exemplo
        var servicoNomes = response.getServicos().stream()
            .map(s -> s.getNome())
            .toList();
        
        assertTrue(servicoNomes.contains("simular-investimento"));
        assertTrue(servicoNomes.contains("perfil-risco"));
        assertTrue(servicoNomes.contains("produtos"));
    }
    
    @Test
    void testObterTelemetriaComContadoresZero() {
        // Configura métricas com contador zero
        when(metricasManager.obterEndpointsComMetricas()).thenReturn(Set.of("produtos"));
        when(metricasManager.obterContadorExecucoes("produtos")).thenReturn(0L);
        when(metricasManager.obterTempoMedioResposta("produtos")).thenReturn(0.0);
        
        // Executa o método
        TelemetriaResponse response = telemetriaService.obterTelemetria();
        
        // Deve retornar dados de exemplo já que não há execuções reais
        assertNotNull(response);
        assertEquals(3, response.getServicos().size());
    }
}