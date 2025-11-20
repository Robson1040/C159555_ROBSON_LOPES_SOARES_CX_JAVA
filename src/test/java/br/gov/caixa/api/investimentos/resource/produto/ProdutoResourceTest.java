package br.gov.caixa.api.investimentos.resource.produto;

import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.service.produto.ProdutoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ProdutoResourceTest {

    @Mock
    ProdutoService produtoService;

    @InjectMocks
    ProdutoResource produtoResource;

    ProdutoResponse produtoMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        produtoMock = new ProdutoResponse(
                1L,
                "Produto A",
                TipoProduto.LCI,
                TipoRentabilidade.PRE,
                BigDecimal.valueOf(100),
                PeriodoRentabilidade.AO_MES,
                null,
                30,
                30,
                true,
                NivelRisco.BAIXO
        );
    }

    @Test
    void testListarProdutos() {
        when(produtoService.listarTodos()).thenReturn(List.of(produtoMock));

        Response response = produtoResource.listarProdutos(null, null, null, null, null, null);

        assertEquals(200, response.getStatus());
        List<?> produtos = (List<?>) response.getEntity();
        assertEquals(1, produtos.size());
    }

    @Test
    void testBuscarPorId() {
        when(produtoService.buscarPorId(1L)).thenReturn(Optional.of(produtoMock));

        Response response = produtoResource.buscarPorId(1L);

        assertEquals(200, response.getStatus());
        ProdutoResponse p = (ProdutoResponse) response.getEntity();
        assertEquals("Produto A", p.nome());
    }

    @Test
    void testCriarProduto() {
        ProdutoRequest produtoRequestMock = new ProdutoRequest(
                "Produto A",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                BigDecimal.valueOf(100),
                PeriodoRentabilidade.AO_ANO,
                null,
                0,
                30,
                true
        );

        when(produtoService.criar(any(ProdutoRequest.class))).thenReturn(produtoMock);

        Response response = produtoResource.criarProduto(produtoRequestMock);

        assertEquals(201, response.getStatus());
        ProdutoResponse p = (ProdutoResponse) response.getEntity();
        assertEquals("Produto A", p.nome());
    }

    @Test
    void testAtualizarProduto() {
        ProdutoRequest produtoRequestMock = new ProdutoRequest(
                "Produto A",
                TipoProduto.POUPANCA,
                TipoRentabilidade.PRE,
                BigDecimal.valueOf(100),
                PeriodoRentabilidade.AO_ANO,
                null,
                0,
                30,
                true
        );

        when(produtoService.atualizar(eq(1L), any(ProdutoRequest.class))).thenReturn(produtoMock);

        Response response = produtoResource.atualizarProduto(1L, produtoRequestMock);

        assertEquals(200, response.getStatus());
        ProdutoResponse p = (ProdutoResponse) response.getEntity();
        assertEquals("Produto A", p.nome());
    }

    @Test
    void testContarProdutos() {
        when(produtoService.contarTodos()).thenReturn(5L);

        Response response = produtoResource.contarProdutos();

        assertEquals(200, response.getStatus());
        ProdutoResource.CountResponse count = (ProdutoResource.CountResponse) response.getEntity();
        assertEquals(5L, count.getTotal());
    }
}
