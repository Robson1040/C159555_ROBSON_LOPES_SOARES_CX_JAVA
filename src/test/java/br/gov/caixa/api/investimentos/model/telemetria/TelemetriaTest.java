package br.gov.caixa.api.investimentos.model.telemetria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelemetriaMetricaTest {

    private TelemetriaMetrica metrico;

    @BeforeEach
    void setUp() {
        metrico = new TelemetriaMetrica("endpoint/teste");
    }

    @Test
    void testConstrutorPadraoInicializaDatas() {
        TelemetriaMetrica m = new TelemetriaMetrica();
        assertNotNull(m.getDataCriacao());
        assertNotNull(m.getUltimaAtualizacao());
    }

    @Test
    void testConstrutorComEndpointInicializaCampos() {
        assertEquals("endpoint/teste", metrico.getEndpoint());
        assertEquals(0L, metrico.getContadorExecucoes());
        assertEquals(0.0, metrico.getTempoTotalExecucao());
        assertEquals(0.0, metrico.getTempoMedioResposta());
        assertNotNull(metrico.getDataCriacao());
        assertNotNull(metrico.getUltimaAtualizacao());
    }

    @Test
    void testIncrementarContador() throws InterruptedException {
        long originalCount = metrico.getContadorExecucoes();
        LocalDateTime originalTime = metrico.getUltimaAtualizacao();

        Thread.sleep(10); // garantir timestamp diferente
        metrico.incrementarContador();

        assertEquals(originalCount + 1, metrico.getContadorExecucoes());
        assertTrue(metrico.getUltimaAtualizacao().isAfter(originalTime));
    }

    @Test
    void testAdicionarTempoExecucaoCalculaMedia() throws InterruptedException {
        metrico.incrementarContador(); // contador = 1
        long tempo1 = 100;
        long tempo2 = 200;

        LocalDateTime before = metrico.getUltimaAtualizacao();
        Thread.sleep(10); // garantir timestamp diferente
        metrico.adicionarTempoExecucao(tempo1);
        assertEquals(tempo1, metrico.getTempoTotalExecucao());
        assertEquals(tempo1, metrico.getTempoMedioResposta());
        assertTrue(metrico.getUltimaAtualizacao().isAfter(before));

        metrico.incrementarContador(); // contador = 2
        metrico.adicionarTempoExecucao(tempo2); // total = 100 + 200 = 300
        assertEquals(300, metrico.getTempoTotalExecucao());
        assertEquals(150, metrico.getTempoMedioResposta()); // média = 300 / 2
    }

    @Test
    void testToStringRetornaFormatoEsperado() {
        metrico.setId(1L);
        metrico.setContadorExecucoes(5L);
        metrico.setTempoMedioResposta(123.45);
        String expected = "TelemetriaMetrica{id=1, endpoint='endpoint/teste', execucoes=5, tempoMedio=123,45}";
        assertEquals(expected, metrico.toString());
    }

    @Test
    void testSettersEGetters() {
        LocalDateTime now = LocalDateTime.now();
        metrico.setDataCriacao(now);
        metrico.setUltimaAtualizacao(now);
        metrico.setContadorExecucoes(10L);
        metrico.setTempoTotalExecucao(500.0);
        metrico.setTempoMedioResposta(50.0);
        metrico.setEndpoint("novoEndpoint");
        metrico.setId(99L);

        assertEquals(now, metrico.getDataCriacao());
        assertEquals(now, metrico.getUltimaAtualizacao());
        assertEquals(10L, metrico.getContadorExecucoes());
        assertEquals(500.0, metrico.getTempoTotalExecucao());
        assertEquals(50.0, metrico.getTempoMedioResposta());
        assertEquals("novoEndpoint", metrico.getEndpoint());
        assertEquals(99L, metrico.getId());
    }

    @Test
    void testFindByEndpointUsandoMock() {
        try (MockedStatic<TelemetriaMetrica> panacheMock = mockStatic(TelemetriaMetrica.class)) {
            TelemetriaMetrica fake = new TelemetriaMetrica("endpoint/mock");
            panacheMock.when(() -> TelemetriaMetrica.findByEndpoint("endpoint/mock")).thenReturn(fake);

            TelemetriaMetrica result = TelemetriaMetrica.findByEndpoint("endpoint/mock");
            assertNotNull(result);
            assertEquals("endpoint/mock", result.getEndpoint());
        }
    }
}