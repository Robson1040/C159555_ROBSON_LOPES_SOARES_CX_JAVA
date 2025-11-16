package br.gov.caixa.api.investimentos.service.telemetria;

import br.gov.caixa.api.investimentos.dto.telemetria.PeriodoTelemetria;
import br.gov.caixa.api.investimentos.dto.telemetria.ServicoTelemetria;
import br.gov.caixa.api.investimentos.dto.telemetria.TelemetriaResponse;
import br.gov.caixa.api.investimentos.repository.telemetria.TelemetriaMetricaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários para TelemetriaService")
class TelemetriaServiceTest {

    @InjectMocks
    private TelemetriaService telemetriaService;

    @Mock
    private MetricasManager metricasManager;

    @Mock
    private TelemetriaMetricaRepository telemetriaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve retornar telemetria completa com período válido")
    void deveRetornarTelemetriaCompletaComPeriodoValido() {
        // Given
        Set<String> endpoints = Set.of("/produtos", "/simulacao");
        LocalDateTime dataInicio = LocalDateTime.of(2023, 11, 15, 10, 0);
        LocalDateTime dataFim = LocalDateTime.of(2023, 11, 16, 16, 30);
        
        when(metricasManager.obterEndpointsComMetricas()).thenReturn(endpoints);
        when(metricasManager.obterContadorExecucoes("/produtos")).thenReturn(15L);
        when(metricasManager.obterTempoMedioResposta("/produtos")).thenReturn(120.5);
        when(metricasManager.obterContadorExecucoes("/simulacao")).thenReturn(8L);
        when(metricasManager.obterTempoMedioResposta("/simulacao")).thenReturn(180.3);
        when(telemetriaRepository.obterDataInicio()).thenReturn(dataInicio);
        when(telemetriaRepository.obterDataFim()).thenReturn(dataFim);

        // When
        TelemetriaResponse response = telemetriaService.obterTelemetria();

        // Then
        assertNotNull(response);
        assertEquals(2, response.getServicos().size());
        assertNotNull(response.getPeriodo());
        assertEquals("2023-11-15 10:00:00", response.getPeriodo().getInicio());
        assertEquals("2023-11-16 16:30:00", response.getPeriodo().getFim());
    }

    @Test
    @DisplayName("Deve retornar período null quando ambas as datas são null")
    void deveRetornarPeriodoNullQuandoAmbasDatasSaoNull() {
        // Given
        Set<String> endpoints = Set.of("/produtos");
        
        when(metricasManager.obterEndpointsComMetricas()).thenReturn(endpoints);
        when(metricasManager.obterContadorExecucoes("/produtos")).thenReturn(5L);
        when(metricasManager.obterTempoMedioResposta("/produtos")).thenReturn(150.0);
        when(telemetriaRepository.obterDataInicio()).thenReturn(null);
        when(telemetriaRepository.obterDataFim()).thenReturn(null);

        // When
        TelemetriaResponse response = telemetriaService.obterTelemetria();

        // Then
        assertNotNull(response);
        assertNull(response.getPeriodo());
        assertEquals(1, response.getServicos().size());
    }

    @Test
    @DisplayName("Deve tratar exceção no repository graciosamente")
    void deveTratarExcecaoNoRepositoryGraciosamente() {
        // Given
        Set<String> endpoints = Set.of("/produtos");
        
        when(metricasManager.obterEndpointsComMetricas()).thenReturn(endpoints);
        when(metricasManager.obterContadorExecucoes("/produtos")).thenReturn(10L);
        when(metricasManager.obterTempoMedioResposta("/produtos")).thenReturn(100.0);
        when(telemetriaRepository.obterDataInicio()).thenThrow(new RuntimeException("Erro no banco"));

        // When
        TelemetriaResponse response = telemetriaService.obterTelemetria();

        // Then
        assertNotNull(response);
        assertEquals(1, response.getServicos().size());
        assertNull(response.getPeriodo());
    }
}
