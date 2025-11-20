package br.gov.caixa.api.investimentos.service.investimento;

import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoResponse;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.exception.produto.ProdutoNotFoundException;
import br.gov.caixa.api.investimentos.mapper.InvestimentoMapper;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.repository.cliente.IPessoaRepository;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import br.gov.caixa.api.investimentos.repository.produto.IProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestimentoServiceTest {

    @Mock
    private InvestimentoMapper investimentoMapper;

    @Mock
    private IPessoaRepository pessoaRepository;

    @Mock
    private IProdutoRepository produtoRepository;

    @Mock
    private IInvestimentoRepository investimentoRepository;

    @InjectMocks
    private InvestimentoService investimentoService;

    private InvestimentoRequest validRequest;
    private Produto validProduto;
    private Pessoa validCliente;
    private Investimento validInvestimento;
    private InvestimentoResponse validResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup valid objects
        validRequest = new InvestimentoRequest(
                1L, // clienteId
                2L, // produtoId
                new BigDecimal("10000.00"), // valor
                12, // prazoMeses
                null, // prazoDias
                null // prazoAnos
        );

        validProduto = new Produto(
                "CDB Test",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("10.0"),
                PeriodoRentabilidade.AO_ANO,
                null,
                0,
                90, // minimoDiasInvestimento
                true
        );
        validProduto.setId(2L);

        validCliente = new Pessoa();
        validCliente.setId(1L);
        validCliente.setNome("Cliente Teste");
        validCliente.setCpf("12345678901");

        validInvestimento = new Investimento();
        validInvestimento.setId(1L);
        validInvestimento.setClienteId(1L);
        validInvestimento.setProdutoId(2L);
        validInvestimento.setValor(new BigDecimal("10000.00"));

        validResponse = new InvestimentoResponse(
                1L, 1L, 2L, new BigDecimal("10000.00"),
                12, null, null, LocalDate.now(),
                TipoProduto.CDB, TipoRentabilidade.PRE,
                new BigDecimal("10.0"), PeriodoRentabilidade.AO_ANO,
                null, 0, 90, true
        );
    }

    @Test
    void criar_WithValidRequest_ShouldReturnInvestimentoResponse() {
        // Given
        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(pessoaRepository.findById(1L)).thenReturn(validCliente);
        when(investimentoMapper.toEntity(validRequest, validProduto)).thenReturn(validInvestimento);
        when(investimentoMapper.toResponse(validInvestimento)).thenReturn(validResponse);

        // When
        InvestimentoResponse response = investimentoService.criar(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(validResponse.id(), response.id());
        assertEquals(validResponse.clienteId(), response.clienteId());
        assertEquals(validResponse.produtoId(), response.produtoId());
        assertEquals(validResponse.valor(), response.valor());

        verify(produtoRepository).findById(2L);
        verify(pessoaRepository).findById(1L);
        verify(investimentoRepository).persist(validInvestimento);
        verify(investimentoMapper).toEntity(validRequest, validProduto);
        verify(investimentoMapper).toResponse(validInvestimento);
    }

    @Test
    void criar_WithNullRequest_ShouldThrowIllegalArgumentException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            investimentoService.criar(null);
        });

        assertEquals("Dados do investimento não podem ser nulos", exception.getMessage());

        verifyNoInteractions(produtoRepository, pessoaRepository, investimentoRepository, investimentoMapper);
    }

    @Test
    void criar_WithNonExistentProduto_ShouldThrowProdutoNotFoundException() {
        // Given
        when(produtoRepository.findById(2L)).thenReturn(null);

        // When & Then
        ProdutoNotFoundException exception = assertThrows(ProdutoNotFoundException.class, () -> {
            investimentoService.criar(validRequest);
        });

        assertEquals("Produto não encontrado com ID: 2", exception.getMessage());

        verify(produtoRepository).findById(2L);
        verifyNoInteractions(pessoaRepository, investimentoRepository, investimentoMapper);
    }

    @Test
    void criar_WithNonExistentCliente_ShouldThrowClienteNotFoundException() {
        // Given
        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(pessoaRepository.findById(1L)).thenReturn(null);

        // When & Then
        ClienteNotFoundException exception = assertThrows(ClienteNotFoundException.class, () -> {
            investimentoService.criar(validRequest);
        });

        assertEquals("Cliente não encontrado com ID: 1", exception.getMessage());

        verify(produtoRepository).findById(2L);
        verify(pessoaRepository).findById(1L);
        verifyNoInteractions(investimentoRepository, investimentoMapper);
    }

    @Test
    void criar_WithPrazoMenorQueMinimo_ShouldThrowIllegalArgumentException() {
        // Given
        InvestimentoRequest requestComPrazoMenor = new InvestimentoRequest(
                1L, 2L, new BigDecimal("10000.00"),
                null, // prazoMeses
                60, // prazoDias menor que mínimo (90)
                null // prazoAnos
        );

        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(pessoaRepository.findById(1L)).thenReturn(validCliente);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            investimentoService.criar(requestComPrazoMenor);
        });

        assertTrue(exception.getMessage().contains("Prazo informado é menor que o mínimo do produto"));
        assertTrue(exception.getMessage().contains("90 dias"));

        verify(produtoRepository).findById(2L);
        verify(pessoaRepository).findById(1L);
        verifyNoInteractions(investimentoRepository, investimentoMapper);
    }

    @Test
    void criar_WithPrazoZero_ShouldNotValidateMinimoDias() {
        // Given
        InvestimentoRequest requestComPrazoZero = new InvestimentoRequest(
                1L, 2L, new BigDecimal("10000.00"),
                null, // prazoMeses
                null, // prazoDias (será 0)
                null // prazoAnos
        );

        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(pessoaRepository.findById(1L)).thenReturn(validCliente);
        when(investimentoMapper.toEntity(requestComPrazoZero, validProduto)).thenReturn(validInvestimento);
        when(investimentoMapper.toResponse(validInvestimento)).thenReturn(validResponse);

        // When
        InvestimentoResponse response = investimentoService.criar(requestComPrazoZero);

        // Then
        assertNotNull(response);
        verify(investimentoRepository).persist(validInvestimento);
    }

    @Test
    void criar_WithPrazoIgualMinimo_ShouldCreate() {
        // Given
        InvestimentoRequest requestComPrazoMinimo = new InvestimentoRequest(
                1L, 2L, new BigDecimal("10000.00"),
                null, // prazoMeses
                90, // prazoDias igual ao mínimo
                null // prazoAnos
        );

        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(pessoaRepository.findById(1L)).thenReturn(validCliente);
        when(investimentoMapper.toEntity(requestComPrazoMinimo, validProduto)).thenReturn(validInvestimento);
        when(investimentoMapper.toResponse(validInvestimento)).thenReturn(validResponse);

        // When
        InvestimentoResponse response = investimentoService.criar(requestComPrazoMinimo);

        // Then
        assertNotNull(response);
        verify(investimentoRepository).persist(validInvestimento);
    }

    @Test
    void criar_WithPrazoMaiorQueMinimo_ShouldCreate() {
        // Given
        InvestimentoRequest requestComPrazoMaior = new InvestimentoRequest(
                1L, 2L, new BigDecimal("10000.00"),
                null, // prazoMeses
                180, // prazoDias maior que mínimo
                null // prazoAnos
        );

        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(pessoaRepository.findById(1L)).thenReturn(validCliente);
        when(investimentoMapper.toEntity(requestComPrazoMaior, validProduto)).thenReturn(validInvestimento);
        when(investimentoMapper.toResponse(validInvestimento)).thenReturn(validResponse);

        // When
        InvestimentoResponse response = investimentoService.criar(requestComPrazoMaior);

        // Then
        assertNotNull(response);
        verify(investimentoRepository).persist(validInvestimento);
    }

    @Test
    void buscarPorCliente_WithValidClienteId_ShouldReturnInvestimentosList() {
        // Given
        Long clienteId = 1L;
        List<Investimento> investimentos = List.of(validInvestimento);
        List<InvestimentoResponse> expectedResponses = List.of(validResponse);

        when(pessoaRepository.findById(clienteId)).thenReturn(validCliente);
        when(investimentoRepository.findByClienteId(clienteId)).thenReturn(investimentos);
        when(investimentoMapper.toResponseList(investimentos)).thenReturn(expectedResponses);

        // When
        List<InvestimentoResponse> responses = investimentoService.buscarPorCliente(clienteId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(expectedResponses.get(0).id(), responses.get(0).id());

        verify(pessoaRepository).findById(clienteId);
        verify(investimentoRepository).findByClienteId(clienteId);
        verify(investimentoMapper).toResponseList(investimentos);
    }

    @Test
    void buscarPorCliente_WithNullClienteId_ShouldThrowIllegalArgumentException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            investimentoService.buscarPorCliente(null);
        });

        assertEquals("ID do cliente não pode ser nulo", exception.getMessage());

        verifyNoInteractions(pessoaRepository, investimentoRepository, investimentoMapper);
    }

    @Test
    void buscarPorCliente_WithNonExistentCliente_ShouldThrowClienteNotFoundException() {
        // Given
        Long clienteId = 999L;
        when(pessoaRepository.findById(clienteId)).thenReturn(null);

        // When & Then
        ClienteNotFoundException exception = assertThrows(ClienteNotFoundException.class, () -> {
            investimentoService.buscarPorCliente(clienteId);
        });

        assertEquals("Cliente não encontrado com ID: 999", exception.getMessage());

        verify(pessoaRepository).findById(clienteId);
        verifyNoInteractions(investimentoRepository, investimentoMapper);
    }

    @Test
    void buscarPorCliente_WithEmptyResults_ShouldReturnEmptyList() {
        // Given
        Long clienteId = 1L;
        List<Investimento> emptyList = new ArrayList<>();
        List<InvestimentoResponse> emptyResponseList = new ArrayList<>();

        when(pessoaRepository.findById(clienteId)).thenReturn(validCliente);
        when(investimentoRepository.findByClienteId(clienteId)).thenReturn(emptyList);
        when(investimentoMapper.toResponseList(emptyList)).thenReturn(emptyResponseList);

        // When
        List<InvestimentoResponse> responses = investimentoService.buscarPorCliente(clienteId);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(pessoaRepository).findById(clienteId);
        verify(investimentoRepository).findByClienteId(clienteId);
        verify(investimentoMapper).toResponseList(emptyList);
    }

    @Test
    void buscarPorCliente_WithMultipleInvestimentos_ShouldReturnAllInvestimentos() {
        // Given
        Long clienteId = 1L;

        Investimento inv1 = new Investimento();
        inv1.setId(1L);
        inv1.setClienteId(clienteId);

        Investimento inv2 = new Investimento();
        inv2.setId(2L);
        inv2.setClienteId(clienteId);

        List<Investimento> investimentos = List.of(inv1, inv2);

        InvestimentoResponse resp1 = new InvestimentoResponse(
                1L, clienteId, 2L, new BigDecimal("5000.00"),
                6, null, null, LocalDate.now(),
                TipoProduto.CDB, TipoRentabilidade.PRE,
                new BigDecimal("8.0"), PeriodoRentabilidade.AO_ANO,
                null, 0, 90, true
        );

        InvestimentoResponse resp2 = new InvestimentoResponse(
                2L, clienteId, 3L, new BigDecimal("15000.00"),
                12, null, null, LocalDate.now(),
                TipoProduto.LCI, TipoRentabilidade.POS,
                new BigDecimal("10.0"), PeriodoRentabilidade.AO_ANO,
                Indice.CDI, 30, 180, true
        );

        List<InvestimentoResponse> expectedResponses = List.of(resp1, resp2);

        when(pessoaRepository.findById(clienteId)).thenReturn(validCliente);
        when(investimentoRepository.findByClienteId(clienteId)).thenReturn(investimentos);
        when(investimentoMapper.toResponseList(investimentos)).thenReturn(expectedResponses);

        // When
        List<InvestimentoResponse> responses = investimentoService.buscarPorCliente(clienteId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals(2L, responses.get(1).id());

        verify(pessoaRepository).findById(clienteId);
        verify(investimentoRepository).findByClienteId(clienteId);
        verify(investimentoMapper).toResponseList(investimentos);
    }

    @Test
    void criar_WithPrazoEmMeses_ShouldConvertToDaysCorrectly() {
        // Given
        InvestimentoRequest requestComMeses = new InvestimentoRequest(
                1L, 2L, new BigDecimal("10000.00"),
                4, // 4 meses = 120 dias
                null, // prazoDias
                null // prazoAnos
        );

        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(pessoaRepository.findById(1L)).thenReturn(validCliente);
        when(investimentoMapper.toEntity(requestComMeses, validProduto)).thenReturn(validInvestimento);
        when(investimentoMapper.toResponse(validInvestimento)).thenReturn(validResponse);

        // When
        InvestimentoResponse response = investimentoService.criar(requestComMeses);

        // Then
        assertNotNull(response);
        // 4 meses * 30 = 120 dias, que é > 90 (mínimo do produto)
        verify(investimentoRepository).persist(validInvestimento);
    }

    @Test
    void criar_WithPrazoEmAnos_ShouldConvertToDaysCorrectly() {
        // Given
        InvestimentoRequest requestComAnos = new InvestimentoRequest(
                1L, 2L, new BigDecimal("10000.00"),
                null, // prazoMeses
                null, // prazoDias
                1// 1 ano = 365 dias
        );

        when(produtoRepository.findById(2L)).thenReturn(validProduto);
        when(pessoaRepository.findById(1L)).thenReturn(validCliente);
        when(investimentoMapper.toEntity(requestComAnos, validProduto)).thenReturn(validInvestimento);
        when(investimentoMapper.toResponse(validInvestimento)).thenReturn(validResponse);

        // When
        InvestimentoResponse response = investimentoService.criar(requestComAnos);

        // Then
        assertNotNull(response);
        // 1 ano * 365 = 365 dias, que é > 90 (mínimo do produto)
        verify(investimentoRepository).persist(validInvestimento);
    }
}