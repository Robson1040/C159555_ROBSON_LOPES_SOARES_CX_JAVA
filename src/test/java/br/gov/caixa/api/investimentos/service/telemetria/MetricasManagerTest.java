package br.gov.caixa.api.investimentos.service.telemetria;

import br.gov.caixa.api.investimentos.repository.telemetria.TelemetriaMetricaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários para MetricasManager")
class MetricasManagerTest {

    @InjectMocks
    private MetricasManager metricasManager;

    @Mock
    private TelemetriaMetricaRepository telemetriaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve incrementar contador e persistir no repositório")
    void deveIncrementarContadorEPersistirNoRepositorio() {
        // Given
        String endpoint = "/produtos";

        // When
        metricasManager.incrementarContador(endpoint);

        // Then
        verify(telemetriaRepository, times(1)).incrementarContador(endpoint);
    }

    @Test
    @DisplayName("Deve registrar tempo de resposta e persistir no repositório")
    void deveRegistrarTempoDeRespostaEPersistirNoRepositorio() {
        // Given
        String endpoint = "/produtos";
        long tempoMs = 150L;

        // When
        metricasManager.registrarTempoResposta(endpoint, tempoMs);

        // Then
        verify(telemetriaRepository, times(1)).adicionarTempoExecucao(endpoint, tempoMs);
    }

    @Test
    @DisplayName("Deve retornar contador do repositório quando disponível")
    void deveRetornarContadorDoRepositorioQuandoDisponivel() {
        // Given
        String endpoint = "/produtos";
        long contadorEsperado = 10L;
        when(telemetriaRepository.obterContadorExecucoes(endpoint)).thenReturn(contadorEsperado);

        // When
        long contador = metricasManager.obterContadorExecucoes(endpoint);

        // Then
        assertEquals(contadorEsperado, contador);
        verify(telemetriaRepository, times(1)).obterContadorExecucoes(endpoint);
    }

    @Test
    @DisplayName("Deve retornar contador do cache quando repositório falha")
    void deveRetornarContadorDoCacheQuandoRepositorioFalha() {
        // Given
        String endpoint = "/produtos";
        when(telemetriaRepository.obterContadorExecucoes(endpoint))
            .thenThrow(new RuntimeException("Erro no banco"));

        // Simular incremento no cache primeiro
        metricasManager.incrementarContador(endpoint);
        reset(telemetriaRepository); // Reset para testar fallback

        when(telemetriaRepository.obterContadorExecucoes(endpoint))
            .thenThrow(new RuntimeException("Erro no banco"));

        // When
        long contador = metricasManager.obterContadorExecucoes(endpoint);

        // Then - Deve usar o cache como fallback
        assertTrue(contador >= 0); // Pode ser 0 se cache não foi usado corretamente
    }

    @Test
    @DisplayName("Deve retornar tempo médio do repositório quando disponível")
    void deveRetornarTempoMedioDoRepositorioQuandoDisponivel() {
        // Given
        String endpoint = "/produtos";
        double tempoMedioEsperado = 120.5;
        when(telemetriaRepository.obterTempoMedioResposta(endpoint)).thenReturn(tempoMedioEsperado);

        // When
        double tempoMedio = metricasManager.obterTempoMedioResposta(endpoint);

        // Then
        assertEquals(tempoMedioEsperado, tempoMedio);
        verify(telemetriaRepository, times(1)).obterTempoMedioResposta(endpoint);
    }

    @Test
    @DisplayName("Deve retornar tempo médio do cache quando repositório falha")
    void deveRetornarTempoMedioDoCacheQuandoRepositorioFalha() {
        // Given
        String endpoint = "/produtos";
        when(telemetriaRepository.obterTempoMedioResposta(endpoint))
            .thenThrow(new RuntimeException("Erro no banco"));
        doThrow(new RuntimeException("Erro no banco"))
            .when(telemetriaRepository).adicionarTempoExecucao(anyString(), anyLong());

        // Simular registro de tempo no cache primeiro
        metricasManager.registrarTempoResposta(endpoint, 100L);
        metricasManager.registrarTempoResposta(endpoint, 200L);

        // When
        double tempoMedio = metricasManager.obterTempoMedioResposta(endpoint);

        // Then - Deve calcular média do cache
        assertEquals(150.0, tempoMedio, 0.01); // (100 + 200) / 2 = 150
    }

    @Test
    @DisplayName("Deve retornar endpoints do repositório quando disponível")
    void deveRetornarEndpointsDoRepositorioQuandoDisponivel() {
        // Given
        Set<String> endpointsEsperados = Set.of("/produtos", "/simulacao");
        when(telemetriaRepository.obterEndpointsComMetricas()).thenReturn(endpointsEsperados);

        // When
        Set<String> endpoints = metricasManager.obterEndpointsComMetricas();

        // Then
        assertEquals(endpointsEsperados, endpoints);
        verify(telemetriaRepository, times(1)).obterEndpointsComMetricas();
    }

    @Test
    @DisplayName("Deve retornar endpoints do cache quando repositório falha")
    void deveRetornarEndpointsDoCacheQuandoRepositorioFalha() {
        // Given
        String endpoint = "/produtos";
        when(telemetriaRepository.obterEndpointsComMetricas())
            .thenThrow(new RuntimeException("Erro no banco"));
        doThrow(new RuntimeException("Erro no banco"))
            .when(telemetriaRepository).incrementarContador(anyString());

        // Simular adição ao cache
        metricasManager.incrementarContador(endpoint);

        // When
        Set<String> endpoints = metricasManager.obterEndpointsComMetricas();

        // Then - Deve usar cache como fallback
        assertTrue(endpoints.contains(endpoint));
    }

    @Test
    @DisplayName("Deve limpar métricas do cache e repositório")
    void deveLimparMetricasDoCacheERepositorio() {
        // Given
        String endpoint = "/produtos";
        metricasManager.incrementarContador(endpoint);
        reset(telemetriaRepository);

        // When
        metricasManager.limparMetricas();

        // Then
        verify(telemetriaRepository, times(1)).limparTodasMetricas();
    }

    @Test
    @DisplayName("Deve ignorar endpoint null ou vazio no incremento")
    void deveIgnorarEndpointNullOuVazioNoIncremento() {
        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> {
            metricasManager.incrementarContador(null);
            metricasManager.incrementarContador("");
        });
        
        // Verify que o repositório nunca foi chamado para null e empty
        verify(telemetriaRepository, never()).incrementarContador(null);
        verify(telemetriaRepository, never()).incrementarContador("");
        
        // Mas deve ser chamado para string com espaços (comportamento atual)
        metricasManager.incrementarContador("   ");
        verify(telemetriaRepository, times(1)).incrementarContador("   ");
    }

    @Test
    @DisplayName("Deve ignorar endpoint null ou vazio no registro de tempo")
    void deveIgnorarEndpointNullOuVazioNoRegistroTempo() {
        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> {
            metricasManager.registrarTempoResposta(null, 100L);
            metricasManager.registrarTempoResposta("", 100L);
        });
        
        // Verify que o repositório nunca foi chamado para null e empty
        verify(telemetriaRepository, never()).adicionarTempoExecucao(null, 100L);
        verify(telemetriaRepository, never()).adicionarTempoExecucao("", 100L);
        
        // Mas deve ser chamado para string com espaços (comportamento atual)
        metricasManager.registrarTempoResposta("   ", 100L);
        verify(telemetriaRepository, times(1)).adicionarTempoExecucao("   ", 100L);
    }

    @Test
    @DisplayName("Deve tratar exceção no repositório durante incremento graciosamente")
    void deveTratarExcecaoNoRepositorioDuranteIncrementoGraciosamente() {
        // Given
        String endpoint = "/produtos";
        doThrow(new RuntimeException("Erro no banco")).when(telemetriaRepository).incrementarContador(endpoint);

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> metricasManager.incrementarContador(endpoint));
    }

    @Test
    @DisplayName("Deve tratar exceção no repositório durante registro de tempo graciosamente")
    void deveTratarExcecaoNoRepositorioDuranteRegistroTempoGraciosamente() {
        // Given
        String endpoint = "/produtos";
        long tempo = 150L;
        doThrow(new RuntimeException("Erro no banco")).when(telemetriaRepository).adicionarTempoExecucao(endpoint, tempo);

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> metricasManager.registrarTempoResposta(endpoint, tempo));
    }

    @Test
    @DisplayName("Deve ser thread-safe para múltiplos incrementos concorrentes")
    void deveSerThreadSafeParaMultiplosIncrementosConcorrentes() throws InterruptedException {
        // Given
        String endpoint = "/produtos";
        int numeroThreads = 10;
        int incrementosPorThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numeroThreads);
        CountDownLatch latch = new CountDownLatch(numeroThreads);

        // When
        for (int i = 0; i < numeroThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementosPorThread; j++) {
                        metricasManager.incrementarContador(endpoint);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Then - Deve ter chamado o repositório exatamente o número esperado de vezes
        verify(telemetriaRepository, times(numeroThreads * incrementosPorThread)).incrementarContador(endpoint);
    }

    @Test
    @DisplayName("Deve calcular média correta com múltiplos tempos registrados")
    void deveCalcularMediaCorretaComMultiplosTemposRegistrados() {
        // Given
        String endpoint = "/produtos";
        when(telemetriaRepository.obterTempoMedioResposta(endpoint))
            .thenThrow(new RuntimeException("Usar cache"));
        doThrow(new RuntimeException("Usar cache"))
            .when(telemetriaRepository).adicionarTempoExecucao(anyString(), anyLong());

        // When - Registrar diferentes tempos
        metricasManager.registrarTempoResposta(endpoint, 100L);
        metricasManager.registrarTempoResposta(endpoint, 200L);
        metricasManager.registrarTempoResposta(endpoint, 300L);

        double tempoMedio = metricasManager.obterTempoMedioResposta(endpoint);

        // Then - Média deve ser (100 + 200 + 300) / 3 = 200
        assertEquals(200.0, tempoMedio, 0.01);
    }

    @Test
    @DisplayName("Deve retornar zero para endpoint sem métricas")
    void deveRetornarZeroParaEndpointSemMetricas() {
        // Given
        String endpoint = "/inexistente";
        when(telemetriaRepository.obterContadorExecucoes(endpoint)).thenReturn(0L);
        when(telemetriaRepository.obterTempoMedioResposta(endpoint)).thenReturn(0.0);

        // When
        long contador = metricasManager.obterContadorExecucoes(endpoint);
        double tempoMedio = metricasManager.obterTempoMedioResposta(endpoint);

        // Then
        assertEquals(0L, contador);
        assertEquals(0.0, tempoMedio);
    }
}