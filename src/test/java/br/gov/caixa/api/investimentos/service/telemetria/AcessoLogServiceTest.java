package br.gov.caixa.api.investimentos.service.telemetria;

import br.gov.caixa.api.investimentos.model.telemetria.AcessoLog;
import br.gov.caixa.api.investimentos.repository.telemetria.AcessoLogRepository;
import br.gov.caixa.api.investimentos.dto.telemetria.AcessoLogDTO;
import br.gov.caixa.api.investimentos.dto.telemetria.EstatisticasAcessoDTO;
import br.gov.caixa.api.investimentos.mapper.AcessoLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcessoLogServiceTest {
    @Mock
    AcessoLogRepository acessoLogRepository;

    @InjectMocks
    AcessoLogService acessoLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarTodosLogs() {
        AcessoLog log = new AcessoLog();
        when(acessoLogRepository.listarTodos()).thenReturn(Collections.singletonList(log));
        List<AcessoLogDTO> dtos = acessoLogService.listarTodosLogs();
        assertEquals(1, dtos.size());
    }

    @Test
    void testBuscarLogsPorUsuario() {
        AcessoLog log = new AcessoLog();
        when(acessoLogRepository.buscarPorUsuario(1L)).thenReturn(Collections.singletonList(log));
        List<AcessoLogDTO> dtos = acessoLogService.buscarLogsPorUsuario(1L);
        assertEquals(1, dtos.size());
    }

    @Test
    void testBuscarLogsPorEndpoint() {
        AcessoLog log = new AcessoLog();
        when(acessoLogRepository.buscarPorEndpoint("/api/teste")).thenReturn(Collections.singletonList(log));
        List<AcessoLogDTO> dtos = acessoLogService.buscarLogsPorEndpoint("/api/teste");
        assertEquals(1, dtos.size());
    }

    @Test
    void testBuscarLogsPorPeriodo() {
        AcessoLog log = new AcessoLog();
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        when(acessoLogRepository.buscarPorPeriodo(inicio, fim)).thenReturn(Collections.singletonList(log));
        List<AcessoLogDTO> dtos = acessoLogService.buscarLogsPorPeriodo(inicio, fim);
        assertEquals(1, dtos.size());
    }

    @Test
    void testBuscarLogsComErro() {
        AcessoLog log = new AcessoLog();
        when(acessoLogRepository.buscarAcessosComErro()).thenReturn(Collections.singletonList(log));
        List<AcessoLogDTO> dtos = acessoLogService.buscarLogsComErro();
        assertEquals(1, dtos.size());
    }

    @Test
    void testBuscarLogsPorStatusCode() {
        AcessoLog log = new AcessoLog();
        when(acessoLogRepository.buscarPorStatusCode(500)).thenReturn(Collections.singletonList(log));
        List<AcessoLogDTO> dtos = acessoLogService.buscarLogsPorStatusCode(500);
        assertEquals(1, dtos.size());
    }

    @Test
    void testBuscarLogsComFiltros() {
        AcessoLog log = new AcessoLog();
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        when(acessoLogRepository.buscarComFiltros(1L, "/api/teste", inicio, fim)).thenReturn(Collections.singletonList(log));
        List<AcessoLogDTO> dtos = acessoLogService.buscarLogsComFiltros(1L, "/api/teste", inicio, fim);
        assertEquals(1, dtos.size());
    }

    @Test
    void testContarTodosLogs() {
        when(acessoLogRepository.contarTodos()).thenReturn(10L);
        assertEquals(10L, acessoLogService.contarTodosLogs());
    }

    @Test
    void testContarLogsPorUsuario() {
        when(acessoLogRepository.contarPorUsuario(1L)).thenReturn(5L);
        assertEquals(5L, acessoLogService.contarLogsPorUsuario(1L));
    }

    @Test
    void testContarLogsPorEndpoint() {
        when(acessoLogRepository.contarPorEndpoint("/api/teste")).thenReturn(3L);
        assertEquals(3L, acessoLogService.contarLogsPorEndpoint("/api/teste"));
    }

    @Test
    void testContarLogsComErro() {
        when(acessoLogRepository.contarAcessosComErro()).thenReturn(2L);
        assertEquals(2L, acessoLogService.contarLogsComErro());
    }

    @Test
    void testObterEstatisticas() {
        when(acessoLogRepository.contarTodos()).thenReturn(10L);
        when(acessoLogRepository.contarAcessosComErro()).thenReturn(2L);
        EstatisticasAcessoDTO stats = acessoLogService.obterEstatisticas();
        assertEquals(10L, stats.totalAcessos());
        assertEquals(8L, stats.acessosComSucesso());
        assertEquals(2L, stats.acessosComErro());
        assertEquals(80.0, stats.taxaSucesso());
        assertEquals(20.0, stats.taxaErro());
    }

    @Test
    void testLimparLogsAntigos() {
        doNothing().when(acessoLogRepository).limparLogsAntigos(30);
        acessoLogService.limparLogsAntigos(30);
        verify(acessoLogRepository, times(1)).limparLogsAntigos(30);
    }

    @Test
    void testLimparTodosLogs() {
        doNothing().when(acessoLogRepository).limparTodosLogs();
        acessoLogService.limparTodosLogs();
        verify(acessoLogRepository, times(1)).limparTodosLogs();
    }

    @Test
    void testRegistrarAcessoSuccess() {
        doNothing().when(acessoLogRepository).persist(any(AcessoLog.class));
        AcessoLog result = acessoLogService.registrarAcesso(1L, "/api/teste", "GET", "/api/teste?param=1", "127.0.0.1", "{}", 200, "{\"ok\":true}", 123L, "JUnit", "", "stack");
        assertNotNull(result);
    }

    @Test
    void testRegistrarAcessoException() {
    doThrow(new RuntimeException("Erro persistencia")).when(acessoLogRepository).persist(any(AcessoLog.class));
    AcessoLog result = acessoLogService.registrarAcesso(1L, "/api/teste", "GET", "/api/teste?param=1", "127.0.0.1", "{}", 200, "{\"ok\":true}", 123L, "JUnit", "", "stack");
    assertNull(result);
    }
}
