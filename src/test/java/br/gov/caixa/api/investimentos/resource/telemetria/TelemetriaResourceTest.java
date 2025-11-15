package br.gov.caixa.api.investimentos.resource.telemetria;

import br.gov.caixa.api.investimentos.dto.telemetria.TelemetriaResponse;
import br.gov.caixa.api.investimentos.model.telemetria.TelemetriaMetrica;
import br.gov.caixa.api.investimentos.repository.telemetria.TelemetriaMetricaRepository;
import br.gov.caixa.api.investimentos.service.telemetria.TelemetriaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelemetriaResourceTest {

    private TelemetriaService telemetriaService;
    private TelemetriaMetricaRepository telemetriaRepository;
    private TelemetriaResource resource;

    @BeforeEach
    void setUp() {
        telemetriaService = mock(TelemetriaService.class);
        telemetriaRepository = mock(TelemetriaMetricaRepository.class);
        resource = new TelemetriaResource();
        resource.telemetriaService = telemetriaService;
        resource.telemetriaRepository = telemetriaRepository;
    }

    @Test
    void obterTelemetria_deveRetornar200() {
        TelemetriaResponse responseMock = new TelemetriaResponse();
        when(telemetriaService.obterTelemetria()).thenReturn(responseMock);

        Response response = resource.obterTelemetria();

        assertEquals(200, response.getStatus());
        assertEquals(responseMock, response.getEntity());
    }

    @Test
    void obterTelemetria_quandoErro_deveRetornar500() {
        when(telemetriaService.obterTelemetria()).thenThrow(new RuntimeException("Erro"));

        Response response = resource.obterTelemetria();

        assertEquals(500, response.getStatus());
        assertTrue(((String)response.getEntity()).contains("Erro ao obter telemetria"));
    }

    @Test
    void obterTelemetriaDetalhada_deveRetornarListaMetricas() {
        List<TelemetriaMetrica> metricas = new ArrayList<>();
        metricas.add(new TelemetriaMetrica("endpoint1"));
        when(telemetriaRepository.listAll()).thenReturn(metricas);

        Response response = resource.obterTelemetriaDetalhada();

        assertEquals(200, response.getStatus());
        assertEquals(metricas, response.getEntity());
    }

    @Test
    void obterTelemetriaDetalhada_quandoErro_deveRetornar500() {
        when(telemetriaRepository.listAll()).thenThrow(new RuntimeException("Erro"));

        Response response = resource.obterTelemetriaDetalhada();

        assertEquals(500, response.getStatus());
        assertTrue(((String)response.getEntity()).contains("Erro ao obter telemetria detalhada"));
    }

    @Test
    void obterMaisAcessados_deveRetornarListaMetricas() {
        List<TelemetriaMetrica> metricas = new ArrayList<>();
        metricas.add(new TelemetriaMetrica("endpoint1"));
        when(telemetriaRepository.listarMaisAcessadas(5)).thenReturn(metricas);

        Response response = resource.obterMaisAcessados(5);

        assertEquals(200, response.getStatus());
        assertEquals(metricas, response.getEntity());
    }

    @Test
    void obterMaisAcessados_quandoErro_deveRetornar500() {
        when(telemetriaRepository.listarMaisAcessadas(5)).thenThrow(new RuntimeException("Erro"));

        Response response = resource.obterMaisAcessados(5);

        assertEquals(500, response.getStatus());
        assertTrue(((String)response.getEntity()).contains("Erro ao obter endpoints mais acessados"));
    }

    @Test
    void limparMetricas_deveRetornar204() {
        doNothing().when(telemetriaRepository).limparTodasMetricas();

        Response response = resource.limparMetricas();

        assertEquals(204, response.getStatus());
    }

    @Test
    void limparMetricas_quandoErro_deveRetornar500() {
        doThrow(new RuntimeException("Erro")).when(telemetriaRepository).limparTodasMetricas();

        Response response = resource.limparMetricas();

        assertEquals(500, response.getStatus());
        assertTrue(((String)response.getEntity()).contains("Erro ao limpar métricas"));
    }
}