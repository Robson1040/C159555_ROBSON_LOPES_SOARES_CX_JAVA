package br.gov.caixa.api.investimentos.service.produto;

import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.exception.produto.ProdutoNotFoundException;
import br.gov.caixa.api.investimentos.mapper.ProdutoMapper;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.repository.produto.IProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProdutoService - Testes unitários para casos não cobertos")
class ProdutoServiceTest {

    private ProdutoService produtoService;

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private IProdutoRepository produtoRepository;

    private ProdutoRequest produtoRequestValido;
    private Produto produto;
    private ProdutoResponse produtoResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        produtoService = new ProdutoService();
        produtoService.produtoMapper = produtoMapper;
        produtoService.produtoRepository = produtoRepository;

        // Setup de dados de teste
        produtoRequestValido = new ProdutoRequest(
                "CDB Test",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("105.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                30,
                30,
                true
        );

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("CDB Test");

        produtoResponse = new ProdutoResponse(
                1L,
                "CDB Test",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("105.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                30,
                30,
                true,
                NivelRisco.BAIXO
        );
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosOsProdutos() {
        // Given
        List<Produto> produtos = List.of(produto);
        List<ProdutoResponse> produtosResponse = List.of(produtoResponse);

        when(produtoRepository.listAll()).thenReturn(produtos);
        when(produtoMapper.toResponseList(produtos)).thenReturn(produtosResponse);

        // When
        List<ProdutoResponse> resultado = produtoService.listarTodos();

        // Then
        assertEquals(produtosResponse, resultado);
        verify(produtoRepository).listAll();
        verify(produtoMapper).toResponseList(produtos);
    }

    @Test
    @DisplayName("Deve retornar optional empty quando buscar por ID nulo")
    void deveRetornarOptionalEmptyQuandoBuscarPorIdNulo() {
        // When
        Optional<ProdutoResponse> resultado = produtoService.buscarPorId(null);

        // Then
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, never()).findByIdOptional(any());
    }

    @Test
    @DisplayName("Deve buscar produto por tipo")
    void deveBuscarProdutoPorTipo() {
        // Given
        TipoProduto tipo = TipoProduto.CDB;
        List<Produto> produtos = List.of(produto);
        List<ProdutoResponse> produtosResponse = List.of(produtoResponse);

        when(produtoRepository.findByTipo(tipo)).thenReturn(produtos);
        when(produtoMapper.toResponseList(produtos)).thenReturn(produtosResponse);

        // When
        List<ProdutoResponse> resultado = produtoService.buscarPorTipo(tipo);

        // Then
        assertEquals(produtosResponse, resultado);
        verify(produtoRepository).findByTipo(tipo);
        verify(produtoMapper).toResponseList(produtos);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por tipo nulo")
    void deveRetornarListaVaziaQuandoBuscarPorTipoNulo() {
        // When
        List<ProdutoResponse> resultado = produtoService.buscarPorTipo(null);

        // Then
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, never()).findByTipo(any());
    }

    @Test
    @DisplayName("Deve buscar produtos por tipo de rentabilidade")
    void deveBuscarProdutosPorTipoRentabilidade() {
        // Given
        TipoRentabilidade tipoRentabilidade = TipoRentabilidade.POS;
        List<Produto> produtos = List.of(produto);
        List<ProdutoResponse> produtosResponse = List.of(produtoResponse);

        when(produtoRepository.findByTipoRentabilidade(tipoRentabilidade)).thenReturn(produtos);
        when(produtoMapper.toResponseList(produtos)).thenReturn(produtosResponse);

        // When
        List<ProdutoResponse> resultado = produtoService.buscarPorTipoRentabilidade(tipoRentabilidade);

        // Then
        assertEquals(produtosResponse, resultado);
        verify(produtoRepository).findByTipoRentabilidade(tipoRentabilidade);
        verify(produtoMapper).toResponseList(produtos);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por tipo rentabilidade nulo")
    void deveRetornarListaVaziaQuandoBuscarPorTipoRentabilidadeNulo() {
        // When
        List<ProdutoResponse> resultado = produtoService.buscarPorTipoRentabilidade(null);

        // Then
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, never()).findByTipoRentabilidade(any());
    }

    @Test
    @DisplayName("Deve buscar produtos com FGC")
    void deveBuscarProdutosComFgc() {
        // Given
        List<Produto> produtos = List.of(produto);
        List<ProdutoResponse> produtosResponse = List.of(produtoResponse);

        when(produtoRepository.findByFgc(true)).thenReturn(produtos);
        when(produtoMapper.toResponseList(produtos)).thenReturn(produtosResponse);

        // When
        List<ProdutoResponse> resultado = produtoService.buscarProdutosComFgc();

        // Then
        assertEquals(produtosResponse, resultado);
        verify(produtoRepository).findByFgc(true);
        verify(produtoMapper).toResponseList(produtos);
    }

    @Test
    @DisplayName("Deve buscar produtos com liquidez imediata")
    void deveBuscarProdutosComLiquidezImediata() {
        // Given
        List<Produto> produtos = List.of(produto);
        List<ProdutoResponse> produtosResponse = List.of(produtoResponse);

        when(produtoRepository.findComLiquidezImediata()).thenReturn(produtos);
        when(produtoMapper.toResponseList(produtos)).thenReturn(produtosResponse);

        // When
        List<ProdutoResponse> resultado = produtoService.buscarProdutosComLiquidezImediata();

        // Then
        assertEquals(produtosResponse, resultado);
        verify(produtoRepository).findComLiquidezImediata();
        verify(produtoMapper).toResponseList(produtos);
    }

    @Test
    @DisplayName("Deve buscar produtos sem liquidez")
    void deveBuscarProdutosSemLiquidez() {
        // Given
        List<Produto> produtos = List.of(produto);
        List<ProdutoResponse> produtosResponse = List.of(produtoResponse);

        when(produtoRepository.findSemLiquidez()).thenReturn(produtos);
        when(produtoMapper.toResponseList(produtos)).thenReturn(produtosResponse);

        // When
        List<ProdutoResponse> resultado = produtoService.buscarProdutosSemLiquidez();

        // Then
        assertEquals(produtosResponse, resultado);
        verify(produtoRepository).findSemLiquidez();
        verify(produtoMapper).toResponseList(produtos);
    }

    @Test
    @DisplayName("Deve buscar produtos por nome")
    void deveBuscarProdutosPorNome() {
        // Given
        String nome = "CDB";
        List<Produto> produtos = List.of(produto);
        List<ProdutoResponse> produtosResponse = List.of(produtoResponse);

        when(produtoRepository.findByNomeContaining(nome)).thenReturn(produtos);
        when(produtoMapper.toResponseList(produtos)).thenReturn(produtosResponse);

        // When
        List<ProdutoResponse> resultado = produtoService.buscarPorNome(nome);

        // Then
        assertEquals(produtosResponse, resultado);
        verify(produtoRepository).findByNomeContaining(nome);
        verify(produtoMapper).toResponseList(produtos);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por nome nulo")
    void deveRetornarListaVaziaQuandoBuscarPorNomeNulo() {
        // When
        List<ProdutoResponse> resultado = produtoService.buscarPorNome(null);

        // Then
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, never()).findByNomeContaining(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por nome vazio")
    void deveRetornarListaVaziaQuandoBuscarPorNomeVazio() {
        // When
        List<ProdutoResponse> resultado = produtoService.buscarPorNome("   ");

        // Then
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, never()).findByNomeContaining(any());
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        // Given
        when(produtoMapper.toEntity(produtoRequestValido)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        // When
        ProdutoResponse resultado = produtoService.criar(produtoRequestValido);

        // Then
        assertEquals(produtoResponse, resultado);
        verify(produtoMapper).toEntity(produtoRequestValido);
        verify(produtoRepository).persist(produto);
        verify(produtoMapper).toResponse(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com request nulo")
    void deveLancarExcecaoAoCriarProdutoComRequestNulo() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.criar(null);
        });

        assertEquals("Dados do produto não podem ser nulos", exception.getMessage());
        verify(produtoRepository, never()).persist(any(Produto.class));
    }

    @Test
    @DisplayName("Deve validar produto pós-fixado sem índice")
    void deveValidarProdutoPosfixadoSemIndice() {
        // Given
        ProdutoRequest requestInvalido = new ProdutoRequest(
                "CDB Inválido",
                TipoProduto.CDB,
                TipoRentabilidade.POS, // Pós-fixado
                new BigDecimal("105.0"),
                PeriodoRentabilidade.AO_ANO,
                null, // Sem índice - inválido para pós-fixado
                30,
                30,
                true
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.criar(requestInvalido);
        });

        assertEquals("Produtos pós-fixados devem ter um índice válido", exception.getMessage());
        verify(produtoRepository, never()).persist(any(Produto.class));
    }

    @Test
    @DisplayName("Deve validar produto pré-fixado com índice")
    void deveValidarProdutoPrefixadoComIndice() {
        // Given
        ProdutoRequest requestInvalido = new ProdutoRequest(
                "Debênture Inválida",
                TipoProduto.DEBENTURE,
                TipoRentabilidade.PRE, // Pré-fixado
                new BigDecimal("8.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI, // Com índice - inválido para pré-fixado
                30,
                30,
                true
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.criar(requestInvalido);
        });

        assertEquals("Produtos pré-fixados não devem ter índice", exception.getMessage());
        verify(produtoRepository, never()).persist(any(Produto.class));
    }

    @Test
    @DisplayName("Deve validar liquidez negativa inválida")
    void deveValidarLiquidezNegativaInvalida() {
        // Given
        ProdutoRequest requestInvalido = new ProdutoRequest(
                "CDB Inválido",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("105.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                -5, // Liquidez inválida (deve ser -1 ou >= 0)
                30,
                true
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.criar(requestInvalido);
        });

        assertEquals("Liquidez deve ser -1 (sem liquidez) ou >= 0", exception.getMessage());
        verify(produtoRepository, never()).persist(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        // Given
        Long id = 1L;
        when(produtoRepository.findById(id)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        // When
        ProdutoResponse resultado = produtoService.atualizar(id, produtoRequestValido);

        // Then
        assertEquals(produtoResponse, resultado);
        verify(produtoRepository).findById(id);
        verify(produtoMapper).updateEntityFromRequest(produto, produtoRequestValido);
        verify(produtoRepository).persist(produto);
        verify(produtoMapper).toResponse(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com ID nulo")
    void deveLancarExcecaoAoAtualizarComIdNulo() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.atualizar(null, produtoRequestValido);
        });

        assertEquals("ID não pode ser nulo", exception.getMessage());
        verify(produtoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com request nulo")
    void deveLancarExcecaoAoAtualizarComRequestNulo() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.atualizar(1L, null);
        });

        assertEquals("Dados do produto não podem ser nulos", exception.getMessage());
        verify(produtoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto não encontrado")
    void deveLancarExcecaoAoAtualizarProdutoNaoEncontrado() {
        // Given
        Long id = 999L;
        when(produtoRepository.findById(id)).thenReturn(null);

        // When & Then
        ProdutoNotFoundException exception = assertThrows(ProdutoNotFoundException.class, () -> {
            produtoService.atualizar(id, produtoRequestValido);
        });

        assertEquals("Produto não encontrado com ID: " + id, exception.getMessage());
        verify(produtoRepository).findById(id);
        verify(produtoMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    @DisplayName("Deve remover produto com sucesso")
    void deveRemoverProdutoComSucesso() {
        // Given
        Long id = 1L;
        when(produtoRepository.deleteById(id)).thenReturn(true);

        // When
        produtoService.remover(id);

        // Then
        verify(produtoRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover com ID nulo")
    void deveLancarExcecaoAoRemoverComIdNulo() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.remover(null);
        });

        assertEquals("ID não pode ser nulo", exception.getMessage());
        verify(produtoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover produto não encontrado")
    void deveLancarExcecaoAoRemoverProdutoNaoEncontrado() {
        // Given
        Long id = 999L;
        when(produtoRepository.deleteById(id)).thenReturn(false);

        // When & Then
        ProdutoNotFoundException exception = assertThrows(ProdutoNotFoundException.class, () -> {
            produtoService.remover(id);
        });

        assertEquals("Produto não encontrado com ID: " + id, exception.getMessage());
        verify(produtoRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve verificar se existe produto por ID")
    void deveVerificarSeExisteProdutoPorId() {
        // Given
        Long id = 1L;
        when(produtoRepository.findByIdOptional(id)).thenReturn(Optional.of(produto));

        // When
        boolean existe = produtoService.existePorId(id);

        // Then
        assertTrue(existe);
        verify(produtoRepository).findByIdOptional(id);
    }

    @Test
    @DisplayName("Deve retornar false quando ID é nulo")
    void deveRetornarFalseQuandoIdENulo() {
        // When
        boolean existe = produtoService.existePorId(null);

        // Then
        assertFalse(existe);
        verify(produtoRepository, never()).findByIdOptional(any());
    }

    @Test
    @DisplayName("Deve contar todos os produtos")
    void deveContarTodosOsProdutos() {
        // Given
        long quantidade = 10L;
        when(produtoRepository.count()).thenReturn(quantidade);

        // When
        long resultado = produtoService.contarTodos();

        // Then
        assertEquals(quantidade, resultado);
        verify(produtoRepository).count();
    }

    @Test
    @DisplayName("Deve limpar todos os produtos")
    void deveLimparTodosOsProdutos() {
        // When
        produtoService.limparTodos();

        // Then
        verify(produtoRepository).deleteAll();
    }

    @Test
    @DisplayName("Deve aceitar liquidez -1 (sem liquidez)")
    void deveAceitarLiquidezMenosUm() {
        // Given
        ProdutoRequest requestValido = new ProdutoRequest(
                "CDB Sem Liquidez",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("105.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                -1, // -1 é válido (sem liquidez)
                30,
                true
        );

        when(produtoMapper.toEntity(requestValido)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> {
            produtoService.criar(requestValido);
        });

        verify(produtoRepository).persist(produto);
    }

    @Test
    @DisplayName("Deve aceitar liquidez 0 ou positiva")
    void deveAceitarLiquidezZeroOuPositiva() {
        // Given
        ProdutoRequest requestValido = new ProdutoRequest(
                "CDB Liquidez Zero",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("105.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                0, // 0 é válido (liquidez imediata)
                30,
                true
        );

        when(produtoMapper.toEntity(requestValido)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> {
            produtoService.criar(requestValido);
        });

        verify(produtoRepository).persist(produto);
    }
}