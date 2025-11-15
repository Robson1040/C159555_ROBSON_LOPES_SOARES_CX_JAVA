package br.gov.caixa.api.investimentos.repository.produto;

import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoRepositoryTest {

    private ProdutoRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new ProdutoRepository());
    }

    @Test
    void findByTipo_returnsProdutos() {
        Produto produto = new Produto();
        PanacheQuery<Produto> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(produto));
        doReturn(query).when(repository).find("tipo", TipoProduto.POUPANCA);

        List<Produto> result = repository.findByTipo(TipoProduto.POUPANCA);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto, result.get(0));
    }

    @Test
    void findByTipoRentabilidade_returnsProdutos() {
        Produto produto = new Produto();
        PanacheQuery<Produto> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(produto));
        doReturn(query).when(repository).find("tipoRentabilidade", TipoRentabilidade.POS);

        List<Produto> result = repository.findByTipoRentabilidade(TipoRentabilidade.POS);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto, result.get(0));
    }

    @Test
    void findByFgc_returnsProdutos() {
        Produto produto = new Produto();
        PanacheQuery<Produto> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(produto));
        doReturn(query).when(repository).find("fgc", true);

        List<Produto> result = repository.findByFgc(true);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto, result.get(0));
    }

    @Test
    void findComLiquidezImediata_returnsProdutos() {
        Produto produto = new Produto();
        PanacheQuery<Produto> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(produto));
        doReturn(query).when(repository).find("liquidez", 0);

        List<Produto> result = repository.findComLiquidezImediata();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto, result.get(0));
    }

    @Test
    void findSemLiquidez_returnsProdutos() {
        Produto produto = new Produto();
        PanacheQuery<Produto> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(produto));
        doReturn(query).when(repository).find("liquidez", -1);

        List<Produto> result = repository.findSemLiquidez();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto, result.get(0));
    }

    @Test
    void findByNomeContaining_returnsProdutos() {
        Produto produto = new Produto();
        PanacheQuery<Produto> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(produto));
        doReturn(query).when(repository).find("lower(nome) like lower(?1)", "%invest%");

        List<Produto> result = repository.findByNomeContaining("invest");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto, result.get(0));
    }
}