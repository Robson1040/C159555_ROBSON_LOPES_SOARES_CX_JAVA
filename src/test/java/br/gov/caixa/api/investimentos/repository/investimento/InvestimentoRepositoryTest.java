package br.gov.caixa.api.investimentos.repository.investimento;

import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestimentoRepositoryTest {

    private InvestimentoRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new InvestimentoRepository());
    }

    @Test
    void findByClienteId_returnsInvestimentos() {
        Investimento inv = new Investimento();
        PanacheQuery<Investimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(inv));
        doReturn(query).when(repository).find("clienteId", 1L);

        List<Investimento> result = repository.findByClienteId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(inv, result.get(0));
    }

    @Test
    void findByProdutoId_returnsInvestimentos() {
        Investimento inv = new Investimento();
        PanacheQuery<Investimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(inv));
        doReturn(query).when(repository).find("produtoId", 10L);

        List<Investimento> result = repository.findByProdutoId(10L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(inv, result.get(0));
    }

    @Test
    void findByClienteIdOrderByDate_returnsInvestimentosOrdered() {
        Investimento inv = new Investimento();
        PanacheQuery<Investimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(inv));
        doReturn(query).when(repository).find("clienteId = ?1 ORDER BY data DESC", 1L);

        List<Investimento> result = repository.findByClienteIdOrderByDate(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(inv, result.get(0));
    }
}