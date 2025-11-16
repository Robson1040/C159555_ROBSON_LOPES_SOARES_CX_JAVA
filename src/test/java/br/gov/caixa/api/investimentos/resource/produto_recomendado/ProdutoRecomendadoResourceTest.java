package br.gov.caixa.api.investimentos.resource.produto_recomendado;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;

import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.service.produto_recomendado.ProdutoRecomendadoService;
import br.gov.caixa.api.investimentos.helper.auth.JwtAuthorizationHelper;
import br.gov.caixa.api.investimentos.exception.auth.AccessDeniedException;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProdutoRecomendadoResource - Testes unitários para casos não cobertos")
class ProdutoRecomendadoResourceTest {

    private ProdutoRecomendadoResource resource;

    @Mock
    private ProdutoRecomendadoService produtoRecomendadoService;

    @Mock
    private JsonWebToken jwt;

    @Mock
    private JwtAuthorizationHelper authHelper;

    private static final Long CLIENTE_ID_TESTE = 123L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resource = new ProdutoRecomendadoResource();
        resource.produtoRecomendadoService = produtoRecomendadoService;
        resource.jwt = jwt;
        resource.authHelper = authHelper;
    }

    @Test
    @DisplayName("Deve retornar produtos recomendados por cliente com sucesso")
    void deveRetornarProdutosRecomendadosPorClienteComSucesso() {
        // Given
        List<ProdutoResponse> produtosEsperados = List.of(
            createProdutoResponse(1L, "CDB Test", TipoProduto.CDB, NivelRisco.BAIXO),
            createProdutoResponse(2L, "LCI Test", TipoProduto.LCI, NivelRisco.BAIXO)
        );
        
        doNothing().when(authHelper).validarAcessoAoCliente(jwt, CLIENTE_ID_TESTE);
        when(produtoRecomendadoService.buscarProdutosPorCliente(CLIENTE_ID_TESTE))
            .thenReturn(produtosEsperados);

        // When
        Response response = resource.buscarProdutosPorCliente(CLIENTE_ID_TESTE);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(produtosEsperados, response.getEntity());
        verify(authHelper).validarAcessoAoCliente(jwt, CLIENTE_ID_TESTE);
        verify(produtoRecomendadoService).buscarProdutosPorCliente(CLIENTE_ID_TESTE);
    }

    @Test
    @DisplayName("Deve retornar 400 quando ocorre IllegalArgumentException em buscarProdutosPorCliente")
    void deveRetornar400QuandoOcorreIllegalArgumentExceptionEmBuscarPorCliente() {
        // Given
        String mensagemErro = "Cliente ID não pode ser negativo";
        doNothing().when(authHelper).validarAcessoAoCliente(jwt, CLIENTE_ID_TESTE);
        when(produtoRecomendadoService.buscarProdutosPorCliente(CLIENTE_ID_TESTE))
            .thenThrow(new IllegalArgumentException(mensagemErro));

        // When
        Response response = resource.buscarProdutosPorCliente(CLIENTE_ID_TESTE);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ProdutoRecomendadoResource.ErrorResponse errorResponse = 
            (ProdutoRecomendadoResource.ErrorResponse) response.getEntity();
        assertEquals(mensagemErro, errorResponse.message());
    }

    @Test
    @DisplayName("Deve retornar 400 quando ocorre IllegalStateException em buscarProdutosPorCliente")
    void deveRetornar400QuandoOcorreIllegalStateExceptionEmBuscarPorCliente() {
        // Given
        String mensagemErro = "Estado inválido para recomendação";
        doNothing().when(authHelper).validarAcessoAoCliente(jwt, CLIENTE_ID_TESTE);
        when(produtoRecomendadoService.buscarProdutosPorCliente(CLIENTE_ID_TESTE))
            .thenThrow(new IllegalStateException(mensagemErro));

        // When
        Response response = resource.buscarProdutosPorCliente(CLIENTE_ID_TESTE);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ProdutoRecomendadoResource.ErrorResponse errorResponse = 
            (ProdutoRecomendadoResource.ErrorResponse) response.getEntity();
        assertEquals(mensagemErro, errorResponse.message());
    }

    @Test
    @DisplayName("Deve retornar 404 quando cliente não é encontrado")
    void deveRetornar404QuandoClienteNaoEEncontrado() {
        // Given
        String mensagemErro = "Cliente não encontrado";
        doNothing().when(authHelper).validarAcessoAoCliente(jwt, CLIENTE_ID_TESTE);
        when(produtoRecomendadoService.buscarProdutosPorCliente(CLIENTE_ID_TESTE))
            .thenThrow(new ClienteNotFoundException(mensagemErro));

        // When
        Response response = resource.buscarProdutosPorCliente(CLIENTE_ID_TESTE);

        // Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        ProdutoRecomendadoResource.ErrorResponse errorResponse = 
            (ProdutoRecomendadoResource.ErrorResponse) response.getEntity();
        assertEquals(mensagemErro, errorResponse.message());
    }

    @Test
    @DisplayName("Deve retornar 500 quando ocorre erro genérico em buscarProdutosPorCliente")
    void deveRetornar500QuandoOcorreErroGenericoEmBuscarPorCliente() {
        // Given
        doNothing().when(authHelper).validarAcessoAoCliente(jwt, CLIENTE_ID_TESTE);
        when(produtoRecomendadoService.buscarProdutosPorCliente(CLIENTE_ID_TESTE))
            .thenThrow(new RuntimeException("Erro inesperado"));

        // When
        Response response = resource.buscarProdutosPorCliente(CLIENTE_ID_TESTE);

        // Then
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ProdutoRecomendadoResource.ErrorResponse errorResponse = 
            (ProdutoRecomendadoResource.ErrorResponse) response.getEntity();
        assertEquals("Erro interno no servidor", errorResponse.message());
    }

    @Test
    @DisplayName("Deve tratar AccessDeniedException como erro interno")
    void deveTratarAccessDeniedExceptionComoErroInterno() {
        // Given
        doThrow(new AccessDeniedException("Acesso negado"))
            .when(authHelper).validarAcessoAoCliente(jwt, CLIENTE_ID_TESTE);

        // When
        Response response = resource.buscarProdutosPorCliente(CLIENTE_ID_TESTE);

        // Then - AccessDeniedException é capturada pelo catch genérico
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ProdutoRecomendadoResource.ErrorResponse errorResponse = 
            (ProdutoRecomendadoResource.ErrorResponse) response.getEntity();
        assertEquals("Erro interno no servidor", errorResponse.message());
        
        verify(authHelper).validarAcessoAoCliente(jwt, CLIENTE_ID_TESTE);
        verify(produtoRecomendadoService, never()).buscarProdutosPorCliente(any());
    }

    @Test
    @DisplayName("Deve retornar produtos recomendados por perfil com sucesso")
    void deveRetornarProdutosRecomendadosPorPerfilComSucesso() {
        // Given
        String perfil = "Conservador";
        List<ProdutoResponse> produtosEsperados = List.of(
            createProdutoResponse(1L, "Poupança", TipoProduto.POUPANCA, NivelRisco.BAIXO),
            createProdutoResponse(2L, "CDB", TipoProduto.CDB, NivelRisco.BAIXO)
        );
        
        when(produtoRecomendadoService.buscarProdutosPorPerfil(perfil))
            .thenReturn(produtosEsperados);

        // When
        Response response = resource.buscarProdutosPorPerfil(perfil);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(produtosEsperados, response.getEntity());
        verify(produtoRecomendadoService).buscarProdutosPorPerfil(perfil);
    }

    @Test
    @DisplayName("Deve retornar 400 quando perfil é inválido")
    void deveRetornar400QuandoPerfilEInvalido() {
        // Given
        String perfilInvalido = "InvalidProfile";
        String mensagemErro = "Perfil inválido: " + perfilInvalido;
        when(produtoRecomendadoService.buscarProdutosPorPerfil(perfilInvalido))
            .thenThrow(new IllegalArgumentException(mensagemErro));

        // When
        Response response = resource.buscarProdutosPorPerfil(perfilInvalido);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ProdutoRecomendadoResource.ErrorResponse errorResponse = 
            (ProdutoRecomendadoResource.ErrorResponse) response.getEntity();
        assertEquals(mensagemErro, errorResponse.message());
    }

    @Test
    @DisplayName("Deve retornar 500 quando ocorre erro genérico em buscarProdutosPorPerfil")
    void deveRetornar500QuandoOcorreErroGenericoEmBuscarPorPerfil() {
        // Given
        String perfil = "Conservador";
        when(produtoRecomendadoService.buscarProdutosPorPerfil(perfil))
            .thenThrow(new RuntimeException("Erro inesperado"));

        // When
        Response response = resource.buscarProdutosPorPerfil(perfil);

        // Then
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ProdutoRecomendadoResource.ErrorResponse errorResponse = 
            (ProdutoRecomendadoResource.ErrorResponse) response.getEntity();
        assertEquals("Erro interno no servidor", errorResponse.message());
    }

    @Test
    @DisplayName("Deve validar ErrorResponse record")
    void deveValidarErrorResponseRecord() {
        // Given
        String mensagem = "Teste de erro";
        
        // When
        ProdutoRecomendadoResource.ErrorResponse errorResponse = 
            new ProdutoRecomendadoResource.ErrorResponse(mensagem);

        // Then
        assertEquals(mensagem, errorResponse.message());
    }

    @Test
    @DisplayName("Deve testar diferentes tipos de perfil válidos")
    void deveTestarDiferentesTiposDePerfilValidos() {
        // Given
        String[] perfisValidos = {"Conservador", "Moderado", "Agressivo"};
        List<ProdutoResponse> produtosVazios = new ArrayList<>();

        for (String perfil : perfisValidos) {
            when(produtoRecomendadoService.buscarProdutosPorPerfil(perfil))
                .thenReturn(produtosVazios);

            // When
            Response response = resource.buscarProdutosPorPerfil(perfil);

            // Then
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertEquals(produtosVazios, response.getEntity());
        }

        verify(produtoRecomendadoService, times(perfisValidos.length)).buscarProdutosPorPerfil(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há produtos recomendados")
    void deveRetornarListaVaziaQuandoNaoHaProdutosRecomendados() {
        // Given
        String perfil = "Conservador";
        List<ProdutoResponse> produtosVazios = new ArrayList<>();
        when(produtoRecomendadoService.buscarProdutosPorPerfil(perfil))
            .thenReturn(produtosVazios);

        // When
        Response response = resource.buscarProdutosPorPerfil(perfil);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<?> produtos = (List<?>) response.getEntity();
        assertTrue(produtos.isEmpty());
    }

    private ProdutoResponse createProdutoResponse(Long id, String nome, TipoProduto tipo, NivelRisco risco) {
        // Usando construtor com parâmetros mínimos necessários
        return new ProdutoResponse(
            id, 
            nome, 
            tipo, 
            null, // tipoRentabilidade
            null, // percentualRentabilidade
            null, // periodoRentabilidade
            null, // indice
            null, // liquidezDias
            null, // carenciaDias
            null, // temFGC
            risco
        );
    }
}