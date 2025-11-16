package br.gov.caixa.api.investimentos.dto.telemetria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTOs Telemetria - Testes unitários completos")
class TelemetriaDTOTest {

    @Test
    @DisplayName("PeriodoTelemetria - Deve criar com construtor padrão")
    void periodoTelemetria_deveCriarComConstrutorPadrao() {
        // When
        PeriodoTelemetria periodo = new PeriodoTelemetria();

        // Then
        assertNotNull(periodo);
        assertNull(periodo.getInicio());
        assertNull(periodo.getFim());
    }

    @Test
    @DisplayName("PeriodoTelemetria - Deve criar com strings")
    void periodoTelemetria_deveCriarComStrings() {
        // Given
        String inicio = "2024-01-01";
        String fim = "2024-01-31";

        // When
        PeriodoTelemetria periodo = new PeriodoTelemetria(inicio, fim);

        // Then
        assertNotNull(periodo);
        assertEquals(inicio, periodo.getInicio());
        assertEquals(fim, periodo.getFim());
    }

    @Test
    @DisplayName("PeriodoTelemetria - Deve criar com LocalDate")
    void periodoTelemetria_deveCriarComLocalDate() {
        // Given
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fim = LocalDate.of(2024, 1, 31);

        // When
        PeriodoTelemetria periodo = new PeriodoTelemetria(inicio, fim);

        // Then
        assertNotNull(periodo);
        assertEquals("2024-01-01", periodo.getInicio());
        assertEquals("2024-01-31", periodo.getFim());
    }

    @Test
    @DisplayName("PeriodoTelemetria - Deve funcionar com setters")
    void periodoTelemetria_deveFuncionarComSetters() {
        // Given
        PeriodoTelemetria periodo = new PeriodoTelemetria();
        String novoInicio = "2024-02-01";
        String novoFim = "2024-02-29";

        // When
        periodo.setInicio(novoInicio);
        periodo.setFim(novoFim);

        // Then
        assertEquals(novoInicio, periodo.getInicio());
        assertEquals(novoFim, periodo.getFim());
    }

    @Test
    @DisplayName("ServicoTelemetria - Deve criar com construtor padrão")
    void servicoTelemetria_deveCriarComConstrutorPadrao() {
        // When
        ServicoTelemetria servico = new ServicoTelemetria();

        // Then
        assertNotNull(servico);
        assertNull(servico.getNome());
        assertEquals(0L, servico.getQuantidadeChamadas());
        assertEquals(0.0, servico.getMediaTempoRespostaMs());
    }

    @Test
    @DisplayName("ServicoTelemetria - Deve criar com parâmetros")
    void servicoTelemetria_deveCriarComParametros() {
        // Given
        String nome = "/produtos";
        long quantidadeChamadas = 150L;
        double mediaTempoResposta = 125.5;

        // When
        ServicoTelemetria servico = new ServicoTelemetria(nome, quantidadeChamadas, mediaTempoResposta);

        // Then
        assertNotNull(servico);
        assertEquals(nome, servico.getNome());
        assertEquals(quantidadeChamadas, servico.getQuantidadeChamadas());
        assertEquals(mediaTempoResposta, servico.getMediaTempoRespostaMs());
    }

    @Test
    @DisplayName("ServicoTelemetria - Deve funcionar com setters")
    void servicoTelemetria_deveFuncionarComSetters() {
        // Given
        ServicoTelemetria servico = new ServicoTelemetria();
        String nome = "/simulacoes";
        long quantidadeChamadas = 89L;
        double mediaTempoResposta = 250.75;

        // When
        servico.setNome(nome);
        servico.setQuantidadeChamadas(quantidadeChamadas);
        servico.setMediaTempoRespostaMs(mediaTempoResposta);

        // Then
        assertEquals(nome, servico.getNome());
        assertEquals(quantidadeChamadas, servico.getQuantidadeChamadas());
        assertEquals(mediaTempoResposta, servico.getMediaTempoRespostaMs());
    }

    @Test
    @DisplayName("TelemetriaResponse - Deve criar com construtor padrão")
    void telemetriaResponse_deveCriarComConstrutorPadrao() {
        // When
        TelemetriaResponse response = new TelemetriaResponse();

        // Then
        assertNotNull(response);
        assertNull(response.getServicos());
        assertNull(response.getPeriodo());
    }

    @Test
    @DisplayName("TelemetriaResponse - Deve criar com parâmetros")
    void telemetriaResponse_deveCriarComParametros() {
        // Given
        List<ServicoTelemetria> servicos = Arrays.asList(
            new ServicoTelemetria("/produtos", 150L, 125.5),
            new ServicoTelemetria("/simulacoes", 89L, 250.75)
        );
        PeriodoTelemetria periodo = new PeriodoTelemetria("2024-01-01", "2024-01-31");

        // When
        TelemetriaResponse response = new TelemetriaResponse(servicos, periodo);

        // Then
        assertNotNull(response);
        assertEquals(servicos, response.getServicos());
        assertEquals(periodo, response.getPeriodo());
        assertEquals(2, response.getServicos().size());
    }

    @Test
    @DisplayName("TelemetriaResponse - Deve funcionar com setters")
    void telemetriaResponse_deveFuncionarComSetters() {
        // Given
        TelemetriaResponse response = new TelemetriaResponse();
        List<ServicoTelemetria> servicos = Arrays.asList(
            new ServicoTelemetria("/clientes", 75L, 100.25)
        );
        PeriodoTelemetria periodo = new PeriodoTelemetria("2024-02-01", "2024-02-29");

        // When
        response.setServicos(servicos);
        response.setPeriodo(periodo);

        // Then
        assertEquals(servicos, response.getServicos());
        assertEquals(periodo, response.getPeriodo());
        assertEquals(1, response.getServicos().size());
    }

    @Test
    @DisplayName("Integração - Deve funcionar com objetos complexos")
    void integracao_deveFuncionarComObjetosComplexos() {
        // Given
        PeriodoTelemetria periodo = new PeriodoTelemetria(
            LocalDate.of(2024, 1, 1), 
            LocalDate.of(2024, 12, 31)
        );

        List<ServicoTelemetria> servicos = Arrays.asList(
            new ServicoTelemetria("/entrar", 1200L, 45.8),
            new ServicoTelemetria("/produtos", 950L, 125.3),
            new ServicoTelemetria("/simulacoes", 720L, 280.9),
            new ServicoTelemetria("/telemetria", 15L, 35.2)
        );

        // When
        TelemetriaResponse response = new TelemetriaResponse(servicos, periodo);

        // Then
        assertNotNull(response);
        assertEquals(4, response.getServicos().size());
        assertEquals("2024-01-01", response.getPeriodo().getInicio());
        assertEquals("2024-12-31", response.getPeriodo().getFim());

        // Validar dados específicos dos serviços
        ServicoTelemetria primeiroServico = response.getServicos().get(0);
        assertEquals("/entrar", primeiroServico.getNome());
        assertEquals(1200L, primeiroServico.getQuantidadeChamadas());
        assertEquals(45.8, primeiroServico.getMediaTempoRespostaMs());

        ServicoTelemetria ultimoServico = response.getServicos().get(3);
        assertEquals("/telemetria", ultimoServico.getNome());
        assertEquals(15L, ultimoServico.getQuantidadeChamadas());
        assertEquals(35.2, ultimoServico.getMediaTempoRespostaMs());
    }

    @Test
    @DisplayName("Edge cases - Deve funcionar com valores extremos")
    void edgeCases_deveFuncionarComValoresExtremos() {
        // Given
        ServicoTelemetria servicoZero = new ServicoTelemetria("", 0L, 0.0);
        ServicoTelemetria servicoAlto = new ServicoTelemetria("teste-longo-nome-servico", Long.MAX_VALUE, Double.MAX_VALUE);
        
        PeriodoTelemetria periodoVazio = new PeriodoTelemetria("", "");
        
        // When
        TelemetriaResponse response = new TelemetriaResponse(
            Arrays.asList(servicoZero, servicoAlto), 
            periodoVazio
        );

        // Then
        assertNotNull(response);
        assertEquals(2, response.getServicos().size());
        
        assertEquals("", response.getServicos().get(0).getNome());
        assertEquals(0L, response.getServicos().get(0).getQuantidadeChamadas());
        assertEquals(0.0, response.getServicos().get(0).getMediaTempoRespostaMs());
        
        assertEquals("teste-longo-nome-servico", response.getServicos().get(1).getNome());
        assertEquals(Long.MAX_VALUE, response.getServicos().get(1).getQuantidadeChamadas());
        assertEquals(Double.MAX_VALUE, response.getServicos().get(1).getMediaTempoRespostaMs());
        
        assertEquals("", response.getPeriodo().getInicio());
        assertEquals("", response.getPeriodo().getFim());
    }
}