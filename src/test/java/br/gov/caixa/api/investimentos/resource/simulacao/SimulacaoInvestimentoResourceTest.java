package br.gov.caixa.api.investimentos.resource.simulacao;

import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.dto.simulacao.ResultadoSimulacao;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponse;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoInvestimentoResponse;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.mapper.SimulacaoInvestimentoMapper;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import br.gov.caixa.api.investimentos.service.simulacao.SimulacaoInvestimentoService;
import br.gov.caixa.api.investimentos.helper.auth.JwtAuthorizationHelper;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacaoInvestimentoResourceTest {

    @InjectMocks
    private SimulacaoInvestimentoResource resource;

    @Mock
    private SimulacaoInvestimentoService service;

    @Mock
    private JsonWebToken jwt;

    @Mock
    private JwtAuthorizationHelper authHelper;

    @Mock
    private SimulacaoInvestimentoMapper simulacaoMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock padrão para JWT - ADMIN com acesso total
        when(jwt.getGroups()).thenReturn(Set.of("ADMIN"));
    }

    @Test
    void testSimularInvestimento() {
        SimulacaoRequest request = mock(SimulacaoRequest.class);
        when(request.valor()).thenReturn(new BigDecimal("1000"));
        when(request.getPrazoEmMeses()).thenReturn(12);

        ProdutoResponse produtoResponse = new ProdutoResponse(
                1L, "ProdutoX", null, null, null, null, null, null, null, null, NivelRisco.BAIXO
        );
        ResultadoSimulacao resultadoSimulacao = new ResultadoSimulacao(
                new BigDecimal("1100"), new BigDecimal(12), 0, null, null, new BigDecimal("0.10"), new BigDecimal("100"), true, "Cenário Teste"
        );

        SimulacaoResponse responseMock = new SimulacaoResponse(
                produtoResponse,
                resultadoSimulacao,
                LocalDateTime.now(),
                10L,
                1L
        );

        when(service.simularInvestimento(request)).thenReturn(responseMock);

        var response = resource.simularInvestimento(request);

        assertEquals(201, response.getStatus());
        assertEquals(responseMock, response.getEntity());
    }

    @Test
    void testBuscarHistoricoSimulacoes() {
        SimulacaoInvestimento simulacao1 = new SimulacaoInvestimento(
                10L, 100L, "ProdutoA", new BigDecimal("1000"), new BigDecimal("1100"), 12, 0, 1,
                new BigDecimal("0.1"), new BigDecimal("100"), true, "Cenário A"
        );
        SimulacaoInvestimento simulacao2 = new SimulacaoInvestimento(
                10L, 101L, "ProdutoB", new BigDecimal("2000"), new BigDecimal("2200"), 6, 0, 0,
                new BigDecimal("0.05"), new BigDecimal("100"), false, "Cenário B"
        );

        SimulacaoInvestimentoResponse response1 = new SimulacaoInvestimentoResponse(
                1L, 100L, 10L, "ProdutoA", new BigDecimal("1000"), new BigDecimal("1100"), 
                12, 0, 1, LocalDateTime.now(), new BigDecimal("0.1"), new BigDecimal("100"), true, "Cenário A"
        );
        SimulacaoInvestimentoResponse response2 = new SimulacaoInvestimentoResponse(
                2L, 101L, 10L, "ProdutoB", new BigDecimal("2000"), new BigDecimal("2200"), 
                6, 0, 0, LocalDateTime.now(), new BigDecimal("0.05"), new BigDecimal("100"), false, "Cenário B"
        );

        when(service.buscarSimulacoesPorCliente(10L)).thenReturn(List.of(simulacao1, simulacao2));
        when(simulacaoMapper.toResponse(simulacao1)).thenReturn(response1);
        when(simulacaoMapper.toResponse(simulacao2)).thenReturn(response2);

        var response = resource.buscarHistoricoSimulacoes(10L);
        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        var body = (List<SimulacaoInvestimentoResponse>) response.getEntity();
        assertEquals(2, body.size());
        assertTrue(body.stream().anyMatch(r -> r.produto().equals("ProdutoA")));
    }

    @Test
    void testBuscarSimulacaoPorId() {
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento(
                10L, 100L, "ProdutoA", new BigDecimal("1000"), new BigDecimal("1100"), 12, 0, 1,
                new BigDecimal("0.1"), new BigDecimal("100"), true, "Cenário A"
        );
        SimulacaoInvestimentoResponse responseExpected = new SimulacaoInvestimentoResponse(
                1L, 100L, 10L, "ProdutoA", new BigDecimal("1000"), new BigDecimal("1100"), 
                12, 0, 1, LocalDateTime.now(), new BigDecimal("0.1"), new BigDecimal("100"), true, "Cenário A"
        );

        when(service.buscarSimulacaoPorId(1L)).thenReturn(simulacao);
        when(simulacaoMapper.toResponse(simulacao)).thenReturn(responseExpected);

        var response = resource.buscarSimulacaoPorId(1L);
        assertEquals(200, response.getStatus());
        var body = (SimulacaoInvestimentoResponse) response.getEntity();
        assertEquals("ProdutoA", body.produto());
    }

    @Test
    void testBuscarSimulacaoPorIdNaoEncontrado() {
        when(service.buscarSimulacaoPorId(999L)).thenReturn(null);
        Exception exception = assertThrows(RuntimeException.class, () -> resource.buscarSimulacaoPorId(999L));
        assertEquals("Simulação não encontrada com ID: 999", exception.getMessage());
    }

    @Test
    void testBuscarEstatisticasCliente() {
        SimulacaoInvestimentoService.EstatisticasCliente estatisticas = mock(SimulacaoInvestimentoService.EstatisticasCliente.class);
        when(service.getEstatisticasCliente(10L)).thenReturn(estatisticas);

        var response = resource.buscarEstatisticasCliente(10L);
        assertEquals(200, response.getStatus());
        assertEquals(estatisticas, response.getEntity());
    }
}