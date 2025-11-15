package br.gov.caixa.api.investimentos.ml;

import br.gov.caixa.api.investimentos.enums.produto.*;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeradorRecomendacaoMLTest {

    private GeradorRecomendacaoML gerador;

    @BeforeEach
    void setUp() {
        gerador = new GeradorRecomendacaoML();
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_comInvestimentos_retornaOrdenado() {
        Produto p1 = criarProduto(1L, "Produto1", TipoProduto.CDB);
        Produto p2 = criarProduto(2L, "Produto2", TipoProduto.LCI);
        Produto p3 = criarProduto(3L, "Produto3", TipoProduto.LCA);

        Investimento inv1 = new Investimento();
        inv1.produtoId = 1L;
        inv1.valor = BigDecimal.valueOf(1000);
        inv1.tipo = TipoProduto.CDB;
        inv1.tipoRentabilidade = TipoRentabilidade.PRE;
        inv1.periodoRentabilidade = PeriodoRentabilidade.AO_MES;
        inv1.indice = Indice.SELIC;
        inv1.liquidez = 0;
        inv1.fgc = true;
        inv1.minimoDiasInvestimento = 0;

        List<Produto> produtos = List.of(p1, p2, p3);
        List<Investimento> investimentos = List.of(inv1);

        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, produtos);

        assertNotNull(recomendados);
        assertTrue(recomendados.contains(p2) || recomendados.contains(p3)); // ignora p1 (mesmo produto)
        assertFalse(recomendados.contains(p1)); // produto original não deve aparecer
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSimulacao_comSimulacoes_retornaOrdenado() {
        Produto p1 = criarProduto(1L, "Produto1", TipoProduto.CDB);
        Produto p2 = criarProduto(2L, "Produto2", TipoProduto.LCI);

        SimulacaoInvestimento sim1 = new SimulacaoInvestimento();
        sim1.produtoId = 1L;
        sim1.setValorInvestido(BigDecimal.valueOf(2000));
        sim1.setProduto("Produto1");

        List<Produto> produtos = List.of(p1, p2);
        List<SimulacaoInvestimento> simulacoes = List.of(sim1);

        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicaoSimulacao(simulacoes, produtos);

        assertNotNull(recomendados);
        assertTrue(recomendados.contains(p2));
        assertFalse(recomendados.contains(p1)); // ignora o mesmo produto
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSemProdutos_retornaVazio() {
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(List.of(), List.of());
        assertTrue(recomendados.isEmpty());
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSimulacaoSemProdutos_retornaVazio() {
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicaoSimulacao(List.of(), List.of());
        assertTrue(recomendados.isEmpty());
    }

    // --------------------- Helpers ---------------------
    private Produto criarProduto(Long id, String nome, TipoProduto tipo) {
        Produto p = new Produto();
        p.setId(id);
        p.setNome(nome);
        p.setTipo(tipo);
        p.setTipoRentabilidade(TipoRentabilidade.PRE);
        p.setPeriodoRentabilidade(PeriodoRentabilidade.AO_MES);
        p.setIndice(Indice.SELIC);
        p.setLiquidez(0);
        p.setFgc(true);
        p.setMinimoDiasInvestimento(0);
        p.setPontuacao(0);
        return p;
    }
}