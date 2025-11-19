package br.gov.caixa.api.investimentos.repository.simulacao;

import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacaoInvestimentoRepositoryTest {

    private SimulacaoInvestimentoRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new SimulacaoInvestimentoRepository());
    }

    @Test
    void findByClienteId_returnsSimulacoes() {
        SimulacaoInvestimento sim = new SimulacaoInvestimento();
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(sim));
        doReturn(query).when(repository).find("clienteId", 1L);

        List<SimulacaoInvestimento> result = repository.findByClienteId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sim, result.get(0));
    }

    @Test
    void findByClienteIdOrderByDate_returnsSimulacoes() {
        SimulacaoInvestimento sim = new SimulacaoInvestimento();
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(sim));
        doReturn(query).when(repository).find("clienteId = ?1 ORDER BY dataSimulacao DESC", 1L);

        List<SimulacaoInvestimento> result = repository.findByClienteIdOrderByDate(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sim, result.get(0));
    }

    @Test
    void findByProduto_returnsSimulacoes() {
        SimulacaoInvestimento sim = new SimulacaoInvestimento();
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(sim));
        doReturn(query).when(repository).find("produto", "Produto A");

        List<SimulacaoInvestimento> result = repository.findByProduto("Produto A");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sim, result.get(0));
    }

    @Test
    void findByValorInvestidoRange_returnsSimulacoes() {
        SimulacaoInvestimento sim = new SimulacaoInvestimento();
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(sim));
        doReturn(query).when(repository).find("valorInvestido BETWEEN ?1 AND ?2", BigDecimal.valueOf(100), BigDecimal.valueOf(1000));

        List<SimulacaoInvestimento> result = repository.findByValorInvestidoRange(BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByDataRange_returnsSimulacoes() {
        SimulacaoInvestimento sim = new SimulacaoInvestimento();
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(sim));
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();
        doReturn(query).when(repository).find("dataSimulacao BETWEEN ?1 AND ?2", inicio, fim);

        List<SimulacaoInvestimento> result = repository.findByDataRange(inicio, fim);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findSimulacoesComValoresSimulados_returnsSimulacoes() {
        SimulacaoInvestimento sim = new SimulacaoInvestimento();
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(sim));
        doReturn(query).when(repository).find("valorSimulado = true");

        List<SimulacaoInvestimento> result = repository.findSimulacoesComValoresSimulados();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void countByClienteId_returnsCount() {
        doReturn(5L).when(repository).count("clienteId", 1L);
        long result = repository.countByClienteId(1L);
        assertEquals(5L, result);
    }

    @Test
    void findLastByClienteId_returnsSimulacao() {
        SimulacaoInvestimento sim = new SimulacaoInvestimento();
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.firstResult()).thenReturn(sim);
        doReturn(query).when(repository).find("clienteId = ?1 ORDER BY dataSimulacao DESC", 1L);

        SimulacaoInvestimento result = repository.findLastByClienteId(1L);
        assertEquals(sim, result);
    }

    /*
    @Test
    void getTotalInvestidoByClienteId_returnsSum() {
        BigDecimal total = BigDecimal.valueOf(1000);
        doReturn(total).when(repository).find("SELECT SUM(s.valorInvestido) FROM SimulacaoInvestimento s WHERE s.clienteId = ?1", 1L)
                .singleResult();

        BigDecimal result = repository.getTotalInvestidoByClienteId(1L);
        assertEquals(total, result);

        // Testa retorno zero quando resultado é nulo
        doReturn(null).when(repository).find("SELECT SUM(s.valorInvestido) FROM SimulacaoInvestimento s WHERE s.clienteId = ?1", 2L)
                .singleResult();
        assertEquals(BigDecimal.ZERO, repository.getTotalInvestidoByClienteId(2L));
    }
    */



    @Test
    void deveRetornarZeroQuandoNaoHaInvestimentos() {
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.singleResult()).thenReturn(null);
        doReturn(query).when(repository).find(anyString(), (Object) anyLong());

        BigDecimal total = repository.getTotalInvestidoByClienteId(1L);
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void deveRetornarValorQuandoInvestimentoExiste() {
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.singleResult()).thenReturn(BigDecimal.valueOf(1000));
        doReturn(query).when(repository).find(anyString(), (Object) anyLong());

        BigDecimal total = repository.getTotalInvestidoByClienteId(1L);
        assertEquals(BigDecimal.valueOf(1000), total);
    }

    @Test
    void deveConverterQuandoTipoEhDouble() {
        @SuppressWarnings("unchecked")
        PanacheQuery<SimulacaoInvestimento> query = (PanacheQuery<SimulacaoInvestimento>) mock(PanacheQuery.class);
        when(query.singleResult()).thenReturn(1000.50);
        doReturn(query).when(repository).find(anyString(), (Object) anyLong());

        BigDecimal total = repository.getTotalInvestidoByClienteId(1L);
        assertEquals(BigDecimal.valueOf(1000.50), total);
    }

    @Test
    void deveLancarExcecaoQuandoTipoInesperado() {
        PanacheQuery<SimulacaoInvestimento> query = mock(PanacheQuery.class);
        when(query.singleResult()).thenReturn("valor-invalido");
        doReturn(query).when(repository).find(anyString(), (Object) anyLong());

        assertThrows(IllegalStateException.class, () -> repository.getTotalInvestidoByClienteId(1L));
    }




}