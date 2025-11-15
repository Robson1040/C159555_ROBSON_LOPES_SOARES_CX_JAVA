/*
package br.gov.caixa.api.investimentos.repository.telemetria;

import br.gov.caixa.api.investimentos.model.telemetria.TelemetriaMetr        TelemetriaMetrica m = new TelemetriaMetrica();
        m.setDataCriacao(LocalDateTime.now());a;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelemetriaMetricaRepositoryTest {

    private TelemetriaMetricaRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new TelemetriaMetricaRepository());
    }

    @Test
    void findByEndpoint_returnsMetrica() {
        TelemetriaMetrica metrica = new TelemetriaMetrica("endpointA");
        PanacheQuery<TelemetriaMetrica> query = mock(PanacheQuery.class);
        when(query.firstResult()).thenReturn(metrica);
        doReturn(query).when(repository).find("endpoint", "endpointA");

        TelemetriaMetrica result = repository.findByEndpoint("endpointA");
        assertEquals(metrica, result);
    }

    @Test
    void findOrCreateByEndpoint_createsNewWhenNotExists() {
        doReturn(null).when(repository).findByEndpoint("endpointB");

        TelemetriaMetrica metrica = repository.findOrCreateByEndpoint("endpointB");
        assertNotNull(metrica);
        assertEquals("endpointB", metrica.getEndpoint());
    }

    @Test
    void incrementarContador_updatesContador() {
        TelemetriaMetrica metrica = spy(new TelemetriaMetrica("endpointC"));
        doReturn(metrica).when(repository).findOrCreateByEndpoint("endpointC");

        repository.incrementarContador("endpointC");
        verify(metrica).incrementarContador();
        verify(repository).persist(metrica);
    }

    @Test
    void adicionarTempoExecucao_updatesTempo() {
        TelemetriaMetrica metrica = spy(new TelemetriaMetrica("endpointD"));
        doReturn(metrica).when(repository).findOrCreateByEndpoint("endpointD");

        repository.adicionarTempoExecucao("endpointD", 500L);
        verify(metrica).adicionarTempoExecucao(500L);
        verify(repository).persist(metrica);
    }

    @Test
    void registrarTempoExecucao_updatesContadorETempo() {
        TelemetriaMetrica metrica = spy(new TelemetriaMetrica("endpointE"));
        doReturn(metrica).when(repository).findOrCreateByEndpoint("endpointE");

        repository.registrarTempoExecucao("endpointE", 1000L);
        verify(metrica).incrementarContador();
        verify(metrica).adicionarTempoExecucao(1000L);
        verify(repository).persist(metrica);
    }

    @Test
    void obterEndpointsComMetricas_returnsSet() {
        TelemetriaMetrica m1 = new TelemetriaMetrica("A");
        TelemetriaMetrica m2 = new TelemetriaMetrica("B");
        doReturn(List.of(m1, m2)).when(repository).listAll();

        Set<String> endpoints = repository.obterEndpointsComMetricas();
        assertEquals(2, endpoints.size());
        assertTrue(endpoints.contains("A"));
        assertTrue(endpoints.contains("B"));
    }

    @Test
    void obterContadorExecucoes_returnsValueOrZero() {
        TelemetriaMetrica metrica = new TelemetriaMetrica("X");
        metrica.setContadorExecucoes(5L);
        doReturn(metrica).when(repository).findByEndpoint("X");

        assertEquals(5L, repository.obterContadorExecucoes("X"));
        assertEquals(0L, repository.obterContadorExecucoes("Y")); // null case
    }

    @Test
    void obterTempoMedioResposta_returnsValueOrZero() {
        TelemetriaMetrica metrica = new TelemetriaMetrica("X");
        metrica.tempoMedioResposta = 123.45;
        doReturn(metrica).when(repository).findByEndpoint("X");

        assertEquals(123.45, repository.obterTempoMedioResposta("X"));
        assertEquals(0.0, repository.obterTempoMedioResposta("Y")); // null case
    }

    @Test
    void listarPorPeriodo_returnsList() {
        TelemetriaMetrica m = new TelemetriaMetrica("Z");
        PanacheQuery<TelemetriaMetrica> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(m));

        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        doReturn(query).when(repository).find("dataCriacao >= ?1 and dataCriacao <= ?2", inicio, fim);

        List<TelemetriaMetrica> result = repository.listarPorPeriodo(inicio, fim);
        assertEquals(1, result.size());
    }

    @Test
    void listarMaisAcessadas_returnsList() {
        TelemetriaMetrica m = new TelemetriaMetrica("Z");
        PanacheQuery<TelemetriaMetrica> query = mock(PanacheQuery.class);
        when(query.page(0, 10)).thenReturn(query);
        when(query.list()).thenReturn(List.of(m));
        doReturn(query).when(repository).find("order by contadorExecucoes desc");

        List<TelemetriaMetrica> result = repository.listarMaisAcessadas(10);
        assertEquals(1, result.size());
    }

    @Test
    void obterDataInicio_returnsData() {
        TelemetriaMetrica m = new TelemetriaMetrica("Z");
        m.dataCriacao = LocalDateTime.now();
        PanacheQuery<TelemetriaMetrica> query = mock(PanacheQuery.class);
        when(query.firstResult()).thenReturn(m);
        doReturn(query).when(repository).find("order by dataCriacao asc");

        assertEquals(m.getDataCriacao(), repository.obterDataInicio());
    }

    @Test
    void obterDataFim_returnsData() {
        TelemetriaMetrica m = new TelemetriaMetrica("Z");
        m.ultimaAtualizacao = LocalDateTime.now();
        PanacheQuery<TelemetriaMetrica> query = mock(PanacheQuery.class);
        when(query.firstResult()).thenReturn(m);
        doReturn(query).when(repository).find("order by ultimaAtualizacao desc");

        assertEquals(m.ultimaAtualizacao, repository.obterDataFim());
    }

    @Test
    void limparTodasMetricas_callsDeleteAll() {
        TelemetriaMetricaRepository spyRepo = spy(repository);
        spyRepo.limparTodasMetricas();
        verify(spyRepo).deleteAll();
    }
}
*/