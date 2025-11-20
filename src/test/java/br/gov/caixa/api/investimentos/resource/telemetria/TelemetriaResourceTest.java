package br.gov.caixa.api.investimentos.resource.telemetria;

import br.gov.caixa.api.investimentos.dto.telemetria.AcessoLogDTO;
import br.gov.caixa.api.investimentos.dto.telemetria.EstatisticasAcessoDTO;
import br.gov.caixa.api.investimentos.dto.telemetria.TelemetriaResponse;
import br.gov.caixa.api.investimentos.model.telemetria.TelemetriaMetrica;
import br.gov.caixa.api.investimentos.repository.telemetria.TelemetriaMetricaRepository;
import br.gov.caixa.api.investimentos.service.telemetria.AcessoLogService;
import br.gov.caixa.api.investimentos.service.telemetria.TelemetriaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TelemetriaResourceTest {

    private TelemetriaService telemetriaService;
    private TelemetriaMetricaRepository telemetriaRepository;
    private AcessoLogService acessoLogService;
    private TelemetriaResource resource;

    @BeforeEach
    void setUp() {
        telemetriaService = mock(TelemetriaService.class);
        telemetriaRepository = mock(TelemetriaMetricaRepository.class);
        acessoLogService = mock(AcessoLogService.class);
        resource = new TelemetriaResource();
        resource.telemetriaService = telemetriaService;
        resource.telemetriaRepository = telemetriaRepository;
        resource.acessoLogService = acessoLogService;
    }

    // ------------------- TESTES EXISTENTES -------------------

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
        assertTrue(((String) response.getEntity()).contains("Erro ao obter telemetria"));
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
        assertTrue(((String) response.getEntity()).contains("Erro ao obter telemetria detalhada"));
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
    void obterMaisAcessados_quandoErro_deveRetornar400() {
        when(telemetriaRepository.listarMaisAcessadas(5)).thenThrow(new RuntimeException("Erro"));

        Response response = resource.obterMaisAcessados(5);

        assertEquals(400, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Erro ao obter endpoints mais acessados"));
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
        assertTrue(((String) response.getEntity()).contains("Erro ao limpar métricas"));
    }

    // ------------------- NOVOS TESTES PARA LOGS -------------------

    @Test
    void listarLogsAcesso_deveRetornar200() {
        AcessoLogDTO logMock = mock(AcessoLogDTO.class);
        List<AcessoLogDTO> logs = List.of(logMock);
        when(acessoLogService.listarTodosLogs()).thenReturn(logs);

        Response response = resource.listarLogsAcesso();

        assertEquals(200, response.getStatus());
        assertEquals(logs, response.getEntity());
    }

    @Test
    void listarLogsAcesso_quandoErro_deveRetornar500() {
        when(acessoLogService.listarTodosLogs()).thenThrow(new RuntimeException("Erro"));

        Response response = resource.listarLogsAcesso();

        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Erro ao listar logs de acesso"));
    }

    @Test
    void listarLogsAcessoPorUsuario_deveRetornar200() {
        AcessoLogDTO logMock = mock(AcessoLogDTO.class);
        List<AcessoLogDTO> logs = List.of(logMock);
        when(acessoLogService.buscarLogsPorUsuario(10L)).thenReturn(logs);

        Response response = resource.listarLogsAcessoPorUsuario(10L);

        assertEquals(200, response.getStatus());
        assertEquals(logs, response.getEntity());
    }

    @Test
    void listarLogsAcessoPorUsuario_quandoErro_deveRetornar500() {
        when(acessoLogService.buscarLogsPorUsuario(10L)).thenThrow(new RuntimeException("Erro"));

        Response response = resource.listarLogsAcessoPorUsuario(10L);

        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Erro ao listar logs do usuário"));
    }

    @Test
    void listarLogsAcessoComErro_deveRetornar200() {
        AcessoLogDTO logMock = mock(AcessoLogDTO.class);
        List<AcessoLogDTO> logs = List.of(logMock);
        when(acessoLogService.buscarLogsComErro()).thenReturn(logs);

        Response response = resource.listarLogsAcessoComErro();

        assertEquals(200, response.getStatus());
        assertEquals(logs, response.getEntity());
    }

    @Test
    void listarLogsAcessoComErro_quandoErro_deveRetornar500() {
        when(acessoLogService.buscarLogsComErro()).thenThrow(new RuntimeException("Erro"));

        Response response = resource.listarLogsAcessoComErro();

        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Erro ao listar logs com erro"));
    }

    @Test
    void listarLogsAcessoPorStatusCode_deveRetornar200() {
        AcessoLogDTO logMock = mock(AcessoLogDTO.class);
        List<AcessoLogDTO> logs = List.of(logMock);
        when(acessoLogService.buscarLogsPorStatusCode(404)).thenReturn(logs);

        Response response = resource.listarLogsAcessoPorStatusCode(404);

        assertEquals(200, response.getStatus());
        assertEquals(logs, response.getEntity());
    }

    @Test
    void listarLogsAcessoPorStatusCode_quandoErro_deveRetornar500() {
        when(acessoLogService.buscarLogsPorStatusCode(404)).thenThrow(new RuntimeException("Erro"));

        Response response = resource.listarLogsAcessoPorStatusCode(404);

        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Erro ao listar logs por status code"));
    }

    @Test
    void obterEstatisticasAcessoLogs_deveRetornar200() {
        EstatisticasAcessoDTO statsMock = mock(EstatisticasAcessoDTO.class);
        when(acessoLogService.obterEstatisticas()).thenReturn(statsMock);

        Response response = resource.obterEstatisticasAcessoLogs();

        assertEquals(200, response.getStatus());
        assertEquals(statsMock, response.getEntity());
    }

    @Test
    void obterEstatisticasAcessoLogs_quandoErro_deveRetornar500() {
        when(acessoLogService.obterEstatisticas()).thenThrow(new RuntimeException("Erro"));

        Response response = resource.obterEstatisticasAcessoLogs();

        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Erro ao obter estatísticas de acesso"));
    }

    @Test
    void limparLogsAcesso_deveRetornar204() {
        doNothing().when(acessoLogService).limparTodosLogs();

        Response response = resource.limparLogsAcesso();

        assertEquals(204, response.getStatus());
    }

    @Test
    void limparLogsAcesso_quandoErro_deveRetornar500() {
        doThrow(new RuntimeException("Erro")).when(acessoLogService).limparTodosLogs();

        Response response = resource.limparLogsAcesso();

        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Erro ao limpar logs de acesso"));
    }

    @Test
    void limparLogsAcessoAntigos_deveRetornar204() {
        doNothing().when(acessoLogService).limparLogsAntigos(30);

        Response response = resource.limparLogsAcessoAntigos(30);

        assertEquals(204, response.getStatus());
    }

    @Test
    void limparLogsAcessoAntigos_quandoErro_deveRetornar500() {
        doThrow(new RuntimeException("Erro")).when(acessoLogService).limparLogsAntigos(30);

        Response response = resource.limparLogsAcessoAntigos(30);

        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Erro ao limpar logs antigos"));
    }
}