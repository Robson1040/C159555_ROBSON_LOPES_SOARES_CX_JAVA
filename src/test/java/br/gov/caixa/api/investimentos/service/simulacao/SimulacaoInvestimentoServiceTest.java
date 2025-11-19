package br.gov.caixa.api.investimentos.service.simulacao;

import br.gov.caixa.api.investimentos.client.SimuladorIndices;
import br.gov.caixa.api.investimentos.client.SimuladorMercado;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponse;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.exception.produto.NenhumProdutoDisponivelException;
import br.gov.caixa.api.investimentos.mapper.ProdutoMapper;
import br.gov.caixa.api.investimentos.mapper.SimulacaoInvestimentoMapper;
import br.gov.caixa.api.investimentos.ml.GeradorRecomendacaoML;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import br.gov.caixa.api.investimentos.repository.produto.IProdutoRepository;
import br.gov.caixa.api.investimentos.repository.simulacao.ISimulacaoInvestimentoRepository;
import br.gov.caixa.api.investimentos.service.produto.ProdutoService;
import br.gov.caixa.api.investimentos.service.simulacao.SimulacaoInvestimentoService.EstatisticasCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SimulacaoInvestimentoServiceTest {

    @Mock
    private ProdutoService produtoService;

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private SimulacaoInvestimentoMapper simulacaoMapper;

    @Mock
    private SimuladorIndices simuladorIndices;

    @Mock
    private SimuladorMercado simuladorMercado;

    @Mock
    private ISimulacaoInvestimentoRepository simulacaoRepository;

    @Mock
    private IInvestimentoRepository investimentoRepository;

    @Mock
    private IProdutoRepository produtoRepository;

    @Mock
    private GeradorRecomendacaoML geradorRecomendacaoML;

    @InjectMocks
    private SimulacaoInvestimentoService simulacaoService;

    private SimulacaoRequest validRequest;
    private Produto validProduto;
    private List<Produto> produtos;
    private SimulacaoInvestimento simulacaoInvestimento;
    private ProdutoResponse produtoResponse;
    private SimuladorMercado.CenarioMercado cenarioMercado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup valid request
        validRequest = new SimulacaoRequest(
                1L, // clienteId
                2L, // produtoId
                new BigDecimal("10000.00"), // valor
                12, // prazoMeses
                null, // prazoDias
                null, // prazoAnos
                TipoProduto.CDB, // tipoProduto
                "CDB Teste", // nome
                TipoRentabilidade.PRE, // tipoRentabilidade
                null, // indice
                30, // liquidez
                true // fgc
        );

        // Setup valid produto
        validProduto = new Produto(
                "CDB Teste",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("10.0"),
                PeriodoRentabilidade.AO_ANO,
                null,
                30,
                90,
                true
        );
        validProduto.setId(2L);

        produtos = List.of(validProduto);

        // Setup simulação investimento
        simulacaoInvestimento = new SimulacaoInvestimento();
        simulacaoInvestimento.setId(1L);
        simulacaoInvestimento.setClienteId(1L);
        simulacaoInvestimento.setProdutoId(2L);
        simulacaoInvestimento.setDataSimulacao(LocalDateTime.now());

        // Setup produto response
        produtoResponse = new ProdutoResponse(
                2L, "CDB Teste", TipoProduto.CDB, TipoRentabilidade.PRE,
                new BigDecimal("10.0"), PeriodoRentabilidade.AO_ANO,
                null, 30, 90, true, NivelRisco.BAIXO
        );

        // Setup cenário de mercado
        cenarioMercado = new SimuladorMercado.CenarioMercado(
            SimuladorMercado.CenarioEconomico.ESTAVEL,
            BigDecimal.ONE,
            "Cenário base",
            true
        );
    }

    @Test
    void simularInvestimento_WithValidRequestAndProdutoId_ShouldReturnSimulacaoResponse() {
        // Given
        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(investimentoRepository.findByClienteId(1L)).thenReturn(new ArrayList<>());
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(any(), any())).thenReturn(produtos);
        when(simuladorMercado.gerarCenario(TipoProduto.CDB, 12)).thenReturn(cenarioMercado);
        when(simuladorMercado.ajustarRentabilidadePorCenario(any(), any(), any())).thenReturn(new BigDecimal("10.0"));
        when(simulacaoMapper.toEntity(eq(1L), eq(2L), eq("CDB Teste"), eq(new BigDecimal("10000.00")), any()))
                .thenReturn(simulacaoInvestimento);
        when(produtoMapper.toResponse(validProduto)).thenReturn(produtoResponse);

        // When
        SimulacaoResponse response = simulacaoService.simularInvestimento(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(produtoResponse, response.produtoValidado());
        assertEquals(1L, response.clienteId());
        assertEquals(1L, response.simulacaoId());
        assertNotNull(response.resultadoSimulacao());

        verify(produtoRepository).findById(2L);
        verify(simulacaoRepository).persist(simulacaoInvestimento);
        verify(produtoMapper).toResponse(validProduto);
    }

    @Test
    void simularInvestimento_WithoutProdutoId_ShouldUseFiltersToFindProduct() {
        // Given
        SimulacaoRequest requestSemProdutoId = new SimulacaoRequest(
                1L, null, new BigDecimal("10000.00"),
                12, null, null,
                TipoProduto.CDB, "CDB Teste", TipoRentabilidade.PRE,
                null, 30, true
        );

        when(produtoRepository.listAll()).thenReturn(produtos);
        when(investimentoRepository.findByClienteId(1L)).thenReturn(new ArrayList<>());
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(any(), any())).thenReturn(produtos);
        when(simuladorMercado.gerarCenario(TipoProduto.CDB, 12)).thenReturn(cenarioMercado);
        when(simuladorMercado.ajustarRentabilidadePorCenario(any(), any(), any())).thenReturn(new BigDecimal("10.0"));
        when(simulacaoMapper.toEntity(eq(1L), eq(2L), eq("CDB Teste"), eq(new BigDecimal("10000.00")), any()))
                .thenReturn(simulacaoInvestimento);
        when(produtoMapper.toResponse(validProduto)).thenReturn(produtoResponse);

        // When
        SimulacaoResponse response = simulacaoService.simularInvestimento(requestSemProdutoId);

        // Then
        assertNotNull(response);
        assertEquals(produtoResponse, response.produtoValidado());
        verify(produtoRepository).listAll();
        verify(simulacaoRepository).persist(simulacaoInvestimento);
    }

    @Test
    void simularInvestimento_WithNonExistentProdutoId_ShouldUseFilters() {
        // Given
        when(produtoRepository.findById(2L)).thenReturn(null);
        when(produtoRepository.listAll()).thenReturn(produtos);
        when(investimentoRepository.findByClienteId(1L)).thenReturn(new ArrayList<>());
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(any(), any())).thenReturn(produtos);
        when(simuladorMercado.gerarCenario(TipoProduto.CDB, 12)).thenReturn(cenarioMercado);
        when(simuladorMercado.ajustarRentabilidadePorCenario(any(), any(), any())).thenReturn(new BigDecimal("10.0"));
        when(simulacaoMapper.toEntity(eq(1L), eq(2L), eq("CDB Teste"), eq(new BigDecimal("10000.00")), any()))
                .thenReturn(simulacaoInvestimento);
        when(produtoMapper.toResponse(validProduto)).thenReturn(produtoResponse);

        // When
        SimulacaoResponse response = simulacaoService.simularInvestimento(validRequest);

        // Then
        assertNotNull(response);
        verify(produtoRepository).findById(2L);
        verify(produtoRepository).listAll();
    }

    @Test
    void simularInvestimento_WithExistingInvestments_ShouldUseMLRecommendation() {
        // Given
        Investimento investimentoExistente = new Investimento();
        investimentoExistente.setId(1L);
        investimentoExistente.setClienteId(1L);
        investimentoExistente.setProdutoId(3L);

        List<Investimento> investimentosExistentes = List.of(investimentoExistente);

        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(investimentoRepository.findByClienteId(1L)).thenReturn(investimentosExistentes);
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(investimentosExistentes, List.of(validProduto)))
                .thenReturn(produtos);
        when(simuladorMercado.gerarCenario(TipoProduto.CDB, 12)).thenReturn(cenarioMercado);
        when(simuladorMercado.ajustarRentabilidadePorCenario(any(), any(), any())).thenReturn(new BigDecimal("10.0"));
        when(simulacaoMapper.toEntity(eq(1L), eq(2L), eq("CDB Teste"), eq(new BigDecimal("10000.00")), any()))
                .thenReturn(simulacaoInvestimento);
        when(produtoMapper.toResponse(validProduto)).thenReturn(produtoResponse);

        // When
        SimulacaoResponse response = simulacaoService.simularInvestimento(validRequest);

        // Then
        assertNotNull(response);
        verify(investimentoRepository).findByClienteId(1L);
        verify(geradorRecomendacaoML).encontrarProdutosOrdenadosPorAparicao(investimentosExistentes, List.of(validProduto));
    }

    @Test
    void simularInvestimento_WithNoMatchingProducts_ShouldThrowException() {
        // Given
        when(produtoRepository.findById(2L)).thenReturn(null);
        when(produtoRepository.listAll()).thenReturn(new ArrayList<>());
        when(investimentoRepository.findByClienteId(1L)).thenReturn(new ArrayList<>());
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(any(), any())).thenReturn(new ArrayList<>());

        // When & Then
        NenhumProdutoDisponivelException exception = assertThrows(NenhumProdutoDisponivelException.class, () -> {
            simulacaoService.simularInvestimento(validRequest);
        });

        assertEquals("Nenhum produto encontrado com os critérios informados", exception.getMessage());
        verifyNoInteractions(simulacaoRepository, produtoMapper);
    }

    @Test
    void simularInvestimento_WithPosFixedProduct_ShouldUseIndexSimulation() {
        // Given
        Produto produtoPos = new Produto(
                "CDB Pós",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("105.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                30,
                90,
                true
        );
        produtoPos.setId(3L);

        when(produtoRepository.findById(2L)).thenReturn(produtoPos);
        when(investimentoRepository.findByClienteId(1L)).thenReturn(new ArrayList<>());
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(any(), any())).thenReturn(List.of(produtoPos));
        when(simuladorMercado.gerarCenario(TipoProduto.CDB, 12)).thenReturn(cenarioMercado);
        when(simuladorIndices.getTaxaSimulada(Indice.CDI, 12)).thenReturn(new BigDecimal("12.0"));
        when(simuladorMercado.ajustarRentabilidadePorCenario(any(), any(), any())).thenReturn(new BigDecimal("12.0"));
        when(simulacaoMapper.toEntity(eq(1L), eq(3L), eq("CDB Pós"), eq(new BigDecimal("10000.00")), any()))
                .thenReturn(simulacaoInvestimento);
        when(produtoMapper.toResponse(produtoPos)).thenReturn(produtoResponse);

        // When
        SimulacaoResponse response = simulacaoService.simularInvestimento(validRequest);

        // Then
        assertNotNull(response);
        verify(simuladorIndices).getTaxaSimulada(Indice.CDI, 12);
        verify(simuladorMercado).ajustarRentabilidadePorCenario(any(), any(), eq(TipoRentabilidade.POS));
    }



    @Test
    void buscarSimulacoesPorCliente_WithValidClienteId_ShouldReturnSimulacoes() {
        // Given
        Long clienteId = 1L;
        List<SimulacaoInvestimento> expectedSimulacoes = List.of(simulacaoInvestimento);
        when(simulacaoRepository.findByClienteIdOrderByDate(clienteId)).thenReturn(expectedSimulacoes);

        // When
        List<SimulacaoInvestimento> result = simulacaoService.buscarSimulacoesPorCliente(clienteId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedSimulacoes, result);
        verify(simulacaoRepository).findByClienteIdOrderByDate(clienteId);
    }

    @Test
    void buscarSimulacoesPorCliente_WithEmptyResults_ShouldReturnEmptyList() {
        // Given
        Long clienteId = 1L;
        List<SimulacaoInvestimento> emptyList = new ArrayList<>();
        when(simulacaoRepository.findByClienteIdOrderByDate(clienteId)).thenReturn(emptyList);

        // When
        List<SimulacaoInvestimento> result = simulacaoService.buscarSimulacoesPorCliente(clienteId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(simulacaoRepository).findByClienteIdOrderByDate(clienteId);
    }

    @Test
    void buscarSimulacaoPorId_WithValidId_ShouldReturnSimulacao() {
        // Given
        Long simulacaoId = 1L;
        when(simulacaoRepository.findById(simulacaoId)).thenReturn(simulacaoInvestimento);

        // When
        SimulacaoInvestimento result = simulacaoService.buscarSimulacaoPorId(simulacaoId);

        // Then
        assertNotNull(result);
        assertEquals(simulacaoInvestimento, result);
        verify(simulacaoRepository).findById(simulacaoId);
    }

    @Test
    void buscarSimulacaoPorId_WithNonExistentId_ShouldReturnNull() {
        // Given
        Long simulacaoId = 999L;
        when(simulacaoRepository.findById(simulacaoId)).thenReturn(null);

        // When
        SimulacaoInvestimento result = simulacaoService.buscarSimulacaoPorId(simulacaoId);

        // Then
        assertNull(result);
        verify(simulacaoRepository).findById(simulacaoId);
    }

    @Test
    void getEstatisticasCliente_WithValidData_ShouldReturnStatistics() {
        // Given
        Long clienteId = 1L;
        long totalSimulacoes = 5L;
        BigDecimal totalInvestido = new BigDecimal("50000.00");
        BigDecimal mediaEsperada = new BigDecimal("10000.00");

        when(simulacaoRepository.countByClienteId(clienteId)).thenReturn(totalSimulacoes);
        when(simulacaoRepository.getTotalInvestidoByClienteId(clienteId)).thenReturn(totalInvestido);
        when(simulacaoRepository.findLastByClienteId(clienteId)).thenReturn(simulacaoInvestimento);

        // When
        EstatisticasCliente estatisticas = simulacaoService.getEstatisticasCliente(clienteId);

        // Then
        assertNotNull(estatisticas);
        assertEquals(totalSimulacoes, estatisticas.totalSimulacoes());
        assertEquals(totalInvestido, estatisticas.totalInvestido());
        assertEquals(mediaEsperada, estatisticas.mediaValorInvestido());
        assertEquals(simulacaoInvestimento, estatisticas.ultimaSimulacao());

        verify(simulacaoRepository).countByClienteId(clienteId);
        verify(simulacaoRepository).getTotalInvestidoByClienteId(clienteId);
        verify(simulacaoRepository).findLastByClienteId(clienteId);
    }

    @Test
    void getEstatisticasCliente_WithZeroSimulations_ShouldReturnZeroValues() {
        // Given
        Long clienteId = 1L;
        long totalSimulacoes = 0L;
        BigDecimal totalInvestido = BigDecimal.ZERO;

        when(simulacaoRepository.countByClienteId(clienteId)).thenReturn(totalSimulacoes);
        when(simulacaoRepository.getTotalInvestidoByClienteId(clienteId)).thenReturn(totalInvestido);
        when(simulacaoRepository.findLastByClienteId(clienteId)).thenReturn(null);

        // When
        EstatisticasCliente estatisticas = simulacaoService.getEstatisticasCliente(clienteId);

        // Then
        assertNotNull(estatisticas);
        assertEquals(0L, estatisticas.totalSimulacoes());
        assertEquals(BigDecimal.ZERO, estatisticas.totalInvestido());
        assertEquals(BigDecimal.ZERO, estatisticas.mediaValorInvestido());
        assertNull(estatisticas.ultimaSimulacao());
    }

    @Test
    void filtrarProdutos_WithAllFilters_ShouldFilterCorrectly() {
        // Given - Produto que não atende aos filtros
        Produto produtoIncorreto = new Produto(
                "LCI Diferente",
                TipoProduto.LCI, // Tipo diferente
                TipoRentabilidade.POS, // Tipo rentabilidade diferente
                new BigDecimal("8.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI, // Índice diferente
                60, // Liquidez diferente
                180, // Prazo mínimo maior
                false // FGC diferente
        );
        produtoIncorreto.setId(7L);
        List<Produto> produtosMixtos = List.of(validProduto, produtoIncorreto);

        when(produtoRepository.listAll()).thenReturn(produtosMixtos);
        when(investimentoRepository.findByClienteId(1L)).thenReturn(new ArrayList<>());
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(any(), any())).thenReturn(List.of(validProduto));
        when(simuladorMercado.gerarCenario(TipoProduto.CDB, 12)).thenReturn(cenarioMercado);
        when(simuladorMercado.ajustarRentabilidadePorCenario(any(), any(), any())).thenReturn(new BigDecimal("10.0"));
        when(simulacaoMapper.toEntity(eq(1L), eq(2L), eq("CDB Teste"), eq(new BigDecimal("10000.00")), any()))
                .thenReturn(simulacaoInvestimento);
        when(produtoMapper.toResponse(validProduto)).thenReturn(produtoResponse);

        SimulacaoRequest requestComFiltros = new SimulacaoRequest(
                1L, null, new BigDecimal("10000.00"),
                12, null, null,
                TipoProduto.CDB, "CDB Teste", TipoRentabilidade.PRE,
                null, 30, true
        );

        // When
        SimulacaoResponse response = simulacaoService.simularInvestimento(requestComFiltros);

        // Then
        assertNotNull(response);
        // Deve retornar o produto correto (validProduto), não o incorreto
        assertEquals(produtoResponse, response.produtoValidado());
    }

    @Test
    void filtrarProdutos_WithNullProducts_ShouldHandleGracefully() {
        // Given - Lista contém produto null
        List<Produto> produtosComNull = new ArrayList<>();
        produtosComNull.add(null);
        produtosComNull.add(validProduto);

        when(produtoRepository.listAll()).thenReturn(produtosComNull);
        when(investimentoRepository.findByClienteId(1L)).thenReturn(new ArrayList<>());
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(any(), any())).thenReturn(List.of(validProduto));
        when(simuladorMercado.gerarCenario(TipoProduto.CDB, 12)).thenReturn(cenarioMercado);
        when(simuladorMercado.ajustarRentabilidadePorCenario(any(), any(), any())).thenReturn(new BigDecimal("10.0"));
        when(simulacaoMapper.toEntity(eq(1L), eq(2L), eq("CDB Teste"), eq(new BigDecimal("10000.00")), any()))
                .thenReturn(simulacaoInvestimento);
        when(produtoMapper.toResponse(validProduto)).thenReturn(produtoResponse);

        SimulacaoRequest requestSemProdutoId = new SimulacaoRequest(
                1L, null, new BigDecimal("10000.00"),
                12, null, null,
                null, null, null, null, null, null
        );

        // When
        SimulacaoResponse response = simulacaoService.simularInvestimento(requestSemProdutoId);

        // Then
        assertNotNull(response);
        // Deve ignorar o produto null e usar o válido
        assertEquals(produtoResponse, response.produtoValidado());
    }
}