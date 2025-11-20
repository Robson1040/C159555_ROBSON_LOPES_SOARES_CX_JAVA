package br.gov.caixa.api.investimentos.resource.simulacao;

import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoAnoDTO;
import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoDataDTO;
import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoMesDTO;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponseDTO;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import br.gov.caixa.api.investimentos.service.simulacao.SimulacaoInvestimentoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SimulacaoResourceTest {

    @Mock
    private SimulacaoInvestimentoService simulacaoInvestimentoService;

    @InjectMocks
    private SimulacaoResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveListarTodasSimulacoes() {
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();
        simulacao.setId(1L);
        simulacao.setClienteId(2L);
        simulacao.setProduto("CDB");
        simulacao.setValorInvestido(BigDecimal.valueOf(1000));
        simulacao.setValorFinal(BigDecimal.valueOf(1100));
        simulacao.setPrazoMeses(12);
        simulacao.setPrazoDias(0);
        simulacao.setPrazoAnos(1);
        simulacao.setDataSimulacao(LocalDateTime.of(2025, 11, 19, 10, 0));

        when(simulacaoInvestimentoService.listarTodasSimulacoes()).thenReturn(List.of(simulacao));

        Response response = resource.listarTodasSimulacoes();
        assertEquals(200, response.getStatus());
        List<SimulacaoResponseDTO> body = (List<SimulacaoResponseDTO>) response.getEntity();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals("CDB", body.get(0).produto());
    }

    @Test
    void deveAgruparPorProdutoEDia() {
        SimulacaoInvestimento simulacao1 = new SimulacaoInvestimento();
        simulacao1.setProduto("LCI");
        simulacao1.setDataSimulacao(LocalDateTime.of(2025, 11, 19, 10, 0));
        simulacao1.setValorInvestido(BigDecimal.valueOf(500));
        simulacao1.setValorFinal(BigDecimal.valueOf(550));

        SimulacaoInvestimento simulacao2 = new SimulacaoInvestimento();
        simulacao2.setProduto("LCI");
        simulacao2.setDataSimulacao(LocalDateTime.of(2025, 11, 19, 15, 0));
        simulacao2.setValorInvestido(BigDecimal.valueOf(700));
        simulacao2.setValorFinal(BigDecimal.valueOf(770));

        when(simulacaoInvestimentoService.listarTodasSimulacoes()).thenReturn(List.of(simulacao1, simulacao2));

        Response response = resource.agruparPorProdutoEDia();
        assertEquals(200, response.getStatus());
        List<AgrupamentoProdutoDataDTO> body = (List<AgrupamentoProdutoDataDTO>) response.getEntity();
        assertNotNull(body);
        assertEquals(1, body.size());
        AgrupamentoProdutoDataDTO agrupamento = body.get(0);
        assertEquals("LCI", agrupamento.produto());
        assertEquals(2L, agrupamento.quantidadeSimulacoes());
        assertEquals(new BigDecimal("600.00"), agrupamento.mediaValorInvestido());
    }

    @Test
    void deveAgruparPorProdutoEAnoMes() {
        SimulacaoInvestimento simulacao1 = new SimulacaoInvestimento();
        simulacao1.setProduto("CDB");
        simulacao1.setDataSimulacao(LocalDateTime.of(2025, 11, 19, 10, 0));
        simulacao1.setValorInvestido(BigDecimal.valueOf(1000));
        simulacao1.setValorFinal(BigDecimal.valueOf(1100));

        when(simulacaoInvestimentoService.listarTodasSimulacoes()).thenReturn(List.of(simulacao1));

        Response response = resource.agruparPorProdutoEAnoMes();
        assertEquals(200, response.getStatus());
        List<AgrupamentoProdutoMesDTO> body = (List<AgrupamentoProdutoMesDTO>) response.getEntity();
        assertNotNull(body);
        assertEquals(1, body.size());
        AgrupamentoProdutoMesDTO agrupamento = body.get(0);
        assertEquals("CDB", agrupamento.produto());
        assertEquals(1L, agrupamento.quantidadeSimulacoes());
        assertEquals(new BigDecimal("1000.00"), agrupamento.mediaValorInvestido());
    }

    @Test
    void deveAgruparPorProdutoEAno() {
        SimulacaoInvestimento simulacao1 = new SimulacaoInvestimento();
        simulacao1.setProduto("CDB");
        simulacao1.setDataSimulacao(LocalDateTime.of(2025, 11, 19, 10, 0));
        simulacao1.setValorInvestido(BigDecimal.valueOf(1000));
        simulacao1.setValorFinal(BigDecimal.valueOf(1100));

        when(simulacaoInvestimentoService.listarTodasSimulacoes()).thenReturn(List.of(simulacao1));

        Response response = resource.agruparPorProdutoEAno();
        assertEquals(200, response.getStatus());
        List<AgrupamentoProdutoAnoDTO> body = (List<AgrupamentoProdutoAnoDTO>) response.getEntity();
        assertNotNull(body);
        assertEquals(1, body.size());
        AgrupamentoProdutoAnoDTO agrupamento = body.get(0);
        assertEquals("CDB", agrupamento.produto());
        assertEquals(1L, agrupamento.quantidadeSimulacoes());
        assertEquals(new BigDecimal("1000.00"), agrupamento.mediaValorInvestido());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaSimulacoes() {
        when(simulacaoInvestimentoService.listarTodasSimulacoes()).thenReturn(List.of());

        Response response = resource.listarTodasSimulacoes();
        assertEquals(200, response.getStatus());
        List<SimulacaoResponseDTO> body = (List<SimulacaoResponseDTO>) response.getEntity();
        assertNotNull(body);
        assertTrue(body.isEmpty());
    }
}
