package br.gov.caixa.api.investimentos.ml;

import br.gov.caixa.api.investimentos.enums.produto.*;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeradorRecomendacaoMLTest {

    private GeradorRecomendacaoML gerador;
    private List<Produto> todosProdutos;

    @BeforeEach
    void setUp() {
        gerador = new GeradorRecomendacaoML();
        
        // Criar uma lista diversificada de produtos para teste
        todosProdutos = new ArrayList<>();
        
        // Produto 1: CDB Conservador
        Produto produto1 = criarProduto(1L, "CDB Conservador", TipoProduto.CDB);
        produto1.setTipoRentabilidade(TipoRentabilidade.PRE);
        produto1.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        produto1.setIndice(null);
        produto1.setLiquidez(0);
        produto1.setMinimoDiasInvestimento(90);
        produto1.setFgc(true);
        todosProdutos.add(produto1);

        // Produto 2: LCI Pós-fixada
        Produto produto2 = criarProduto(2L, "LCI Moderada", TipoProduto.LCI);
        produto2.setTipoRentabilidade(TipoRentabilidade.POS);
        produto2.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        produto2.setIndice(Indice.CDI);
        produto2.setLiquidez(30);
        produto2.setMinimoDiasInvestimento(180);
        produto2.setFgc(true);
        todosProdutos.add(produto2);

        // Produto 3: Fundo sem FGC
        Produto produto3 = criarProduto(3L, "Fundo Multi", TipoProduto.FUNDO);
        produto3.setTipoRentabilidade(TipoRentabilidade.POS);
        produto3.setPeriodoRentabilidade(PeriodoRentabilidade.AO_MES);
        produto3.setIndice(Indice.IBOVESPA);
        produto3.setLiquidez(1);
        produto3.setMinimoDiasInvestimento(30);
        produto3.setFgc(false);
        todosProdutos.add(produto3);

        // Produto 4: Tesouro Direto
        Produto produto4 = criarProduto(4L, "Tesouro IPCA+", TipoProduto.TESOURO_DIRETO);
        produto4.setTipoRentabilidade(TipoRentabilidade.POS);
        produto4.setPeriodoRentabilidade(PeriodoRentabilidade.PERIODO_TOTAL);
        produto4.setIndice(Indice.IPCA);
        produto4.setLiquidez(-1);
        produto4.setMinimoDiasInvestimento(365);
        produto4.setFgc(false);
        todosProdutos.add(produto4);
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_comInvestimentos_retornaOrdenado() {
        // Given
        Investimento inv1 = criarInvestimento(1L, BigDecimal.valueOf(50000), TipoProduto.CDB);
        List<Investimento> investimentos = List.of(inv1);

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        assertFalse(recomendados.contains(todosProdutos.get(0))); // produto original não deve aparecer
        assertTrue(recomendados.stream().allMatch(p -> p.getPontuacao() > 0));
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_comMultiplosInvestimentos_ordenaCorretamente() {
        // Given
        Investimento inv1 = criarInvestimento(1L, BigDecimal.valueOf(100000), TipoProduto.CDB);
        Investimento inv2 = criarInvestimento(2L, BigDecimal.valueOf(50000), TipoProduto.LCI);
        List<Investimento> investimentos = List.of(inv1, inv2);

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        assertFalse(recomendados.isEmpty());
        
        // Verifica ordenação por pontuação (decrescente)
        for (int i = 0; i < recomendados.size() - 1; i++) {
            assertTrue(recomendados.get(i).getPontuacao() >= recomendados.get(i + 1).getPontuacao());
        }
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_comInvestimentoNullValues_trataGraciosamente() {
        // Given
        Investimento inv = new Investimento();
        inv.setProdutoId(1L);
        inv.setValor(BigDecimal.valueOf(10000));
        inv.setTipo(TipoProduto.CDB);
        inv.setTipoRentabilidade(null); // null
        inv.setPeriodoRentabilidade(null); // null
        inv.setIndice(null); // null
        inv.setLiquidez(null); // null
        inv.setMinimoDiasInvestimento(null); // null
        inv.setFgc(null); // null
        List<Investimento> investimentos = List.of(inv);

        // When & Then - não deve lançar exceção
        assertDoesNotThrow(() -> {
            List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);
            assertNotNull(recomendados);
        });
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSimulacao_comSimulacoes_retornaOrdenado() {
        // Given
        SimulacaoInvestimento sim1 = new SimulacaoInvestimento(100L, 1L, "CDB Test", BigDecimal.valueOf(30000), BigDecimal.valueOf(35000), 12, 365, 1);
        List<SimulacaoInvestimento> simulacoes = List.of(sim1);

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicaoSimulacao(simulacoes, todosProdutos);

        // Then
        assertNotNull(recomendados);
        assertFalse(recomendados.contains(todosProdutos.get(0))); // ignora o mesmo produto
        assertTrue(recomendados.stream().allMatch(p -> p.getPontuacao() > 0));
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSimulacao_comProdutoInexistente_lancaExcecao() {
        // Given
        SimulacaoInvestimento sim = new SimulacaoInvestimento(100L, 999L, "Produto Inexistente", BigDecimal.valueOf(10000), BigDecimal.valueOf(12000), 12, 365, 1);
        List<SimulacaoInvestimento> simulacoes = List.of(sim);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            gerador.encontrarProdutosOrdenadosPorAparicaoSimulacao(simulacoes, todosProdutos);
        });
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSimulacao_comMultiplasSimulacoes_ordenaCorretamente() {
        // Given
        SimulacaoInvestimento sim1 = new SimulacaoInvestimento(100L, 1L, "CDB High", BigDecimal.valueOf(200000), BigDecimal.valueOf(240000), 12, 365, 1);
        SimulacaoInvestimento sim2 = new SimulacaoInvestimento(101L, 2L, "LCI Low", BigDecimal.valueOf(5000), BigDecimal.valueOf(5500), 12, 365, 1);
        List<SimulacaoInvestimento> simulacoes = List.of(sim1, sim2);

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicaoSimulacao(simulacoes, todosProdutos);

        // Then
        assertNotNull(recomendados);
        assertFalse(recomendados.isEmpty());
        
        // Verifica ordenação por pontuação (decrescente)
        for (int i = 0; i < recomendados.size() - 1; i++) {
            assertTrue(recomendados.get(i).getPontuacao() >= recomendados.get(i + 1).getPontuacao());
        }
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_comListasVazias_retornaVazio() {
        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(List.of(), List.of());

        // Then
        assertTrue(recomendados.isEmpty());
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSimulacao_comListasVazias_retornaVazio() {
        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicaoSimulacao(List.of(), List.of());

        // Then
        assertTrue(recomendados.isEmpty());
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_comApenasUmProduto_retornaVazio() {
        // Given
        Investimento inv = criarInvestimento(1L, BigDecimal.valueOf(10000), TipoProduto.CDB);
        List<Investimento> investimentos = List.of(inv);
        List<Produto> apenasUmProduto = List.of(todosProdutos.get(0)); // Apenas o produto do investimento

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, apenasUmProduto);

        // Then
        assertTrue(recomendados.isEmpty());
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSimulacao_comApenasUmProduto_retornaVazio() {
        // Given
        SimulacaoInvestimento sim = new SimulacaoInvestimento(100L, 1L, "CDB Test", BigDecimal.valueOf(10000), BigDecimal.valueOf(12000), 12, 365, 1);
        List<SimulacaoInvestimento> simulacoes = List.of(sim);
        List<Produto> apenasUmProduto = List.of(todosProdutos.get(0)); // Apenas o produto da simulação

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicaoSimulacao(simulacoes, apenasUmProduto);

        // Then
        assertTrue(recomendados.isEmpty());
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_testaDistanciaEuclidiana_comValoresExtremos() {
        // Given - Investimento com valores extremos para testar normalização
        Investimento inv = new Investimento();
        inv.setProdutoId(1L);
        inv.setValor(BigDecimal.valueOf(1000000)); // Valor máximo para normalização
        inv.setTipo(TipoProduto.CDB);
        inv.setTipoRentabilidade(TipoRentabilidade.POS);
        inv.setPeriodoRentabilidade(PeriodoRentabilidade.PERIODO_TOTAL);
        inv.setIndice(Indice.IBOVESPA);
        inv.setLiquidez(365); // Máximo
        inv.setMinimoDiasInvestimento(1800); // Máximo
        inv.setFgc(false);
        List<Investimento> investimentos = List.of(inv);

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        // Deve funcionar mesmo com valores extremos
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_testaDistanciaEuclidiana_comValoresMinimos() {
        // Given - Investimento com valores mínimos
        Investimento inv = new Investimento();
        inv.setProdutoId(1L);
        inv.setValor(BigDecimal.valueOf(1)); // Valor mínimo
        inv.setTipo(TipoProduto.CDB);
        inv.setTipoRentabilidade(TipoRentabilidade.PRE);
        inv.setPeriodoRentabilidade(PeriodoRentabilidade.AO_DIA);
        inv.setIndice(Indice.NENHUM);
        inv.setLiquidez(-1); // Sem liquidez
        inv.setMinimoDiasInvestimento(0); // Mínimo
        inv.setFgc(true);
        List<Investimento> investimentos = List.of(inv);

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        // Deve funcionar mesmo com valores mínimos
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_testaTodosOsTiposProduto() {
        // Given - Investimentos com diferentes tipos para testar normalização
        List<Investimento> investimentos = new ArrayList<>();
        
        for (TipoProduto tipo : TipoProduto.values()) {
            Investimento inv = criarInvestimento(1L, BigDecimal.valueOf(10000), tipo);
            investimentos.add(inv);
        }

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        // Deve funcionar com todos os tipos de produto
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_testaTodosOsIndices() {
        // Given - Investimentos com diferentes índices
        List<Investimento> investimentos = new ArrayList<>();
        
        for (Indice indice : Indice.values()) {
            Investimento inv = criarInvestimento(2L, BigDecimal.valueOf(10000), TipoProduto.LCI);
            inv.setIndice(indice);
            investimentos.add(inv);
        }

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        // Deve funcionar com todos os índices
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_testaTodosPeriodosRentabilidade() {
        // Given - Investimentos com diferentes períodos
        List<Investimento> investimentos = new ArrayList<>();
        
        for (PeriodoRentabilidade periodo : PeriodoRentabilidade.values()) {
            Investimento inv = criarInvestimento(2L, BigDecimal.valueOf(10000), TipoProduto.LCI);
            inv.setPeriodoRentabilidade(periodo);
            investimentos.add(inv);
        }

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        // Deve funcionar com todos os períodos de rentabilidade
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_testaTodosTiposRentabilidade() {
        // Given - Investimentos com diferentes tipos de rentabilidade
        List<Investimento> investimentos = new ArrayList<>();
        
        for (TipoRentabilidade tipoRent : TipoRentabilidade.values()) {
            Investimento inv = criarInvestimento(2L, BigDecimal.valueOf(10000), TipoProduto.LCI);
            inv.setTipoRentabilidade(tipoRent);
            investimentos.add(inv);
        }

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        // Deve funcionar com todos os tipos de rentabilidade
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicao_comMesmosProdutoIds_testaBehavior() {
        // Given - Teste com um investimento específico 
        List<Investimento> investimentos = new ArrayList<>();
        
        // Usar apenas um investimento para testar o comportamento específico
        Investimento inv = criarInvestimento(1L, BigDecimal.valueOf(10000), TipoProduto.CDB);
        investimentos.add(inv);

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicao(investimentos, todosProdutos);

        // Then
        assertNotNull(recomendados);
        // O produto com ID 1 não deve estar nas recomendações
        assertFalse(recomendados.stream().anyMatch(p -> p.getId().equals(1L)));
    }

    @Test
    void encontrarProdutosOrdenadosPorAparicaoSimulacao_comMesmosProdutoIds_testaBehavior() {
        // Given - Teste com uma simulação específica
        List<SimulacaoInvestimento> simulacoes = new ArrayList<>();
        
        // Usar apenas uma simulação para testar o comportamento específico
        SimulacaoInvestimento sim = new SimulacaoInvestimento(
            100L, 1L, "CDB Test", 
            BigDecimal.valueOf(10000), BigDecimal.valueOf(12000), 12, 365, 1
        );
        simulacoes.add(sim);

        // When
        List<Produto> recomendados = gerador.encontrarProdutosOrdenadosPorAparicaoSimulacao(simulacoes, todosProdutos);

        // Then
        assertNotNull(recomendados);
        // O produto com ID 1 não deve estar nas recomendações
        assertFalse(recomendados.stream().anyMatch(p -> p.getId().equals(1L)));
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

    private Investimento criarInvestimento(Long produtoId, BigDecimal valor, TipoProduto tipo) {
        Investimento inv = new Investimento();
        inv.setProdutoId(produtoId);
        inv.setValor(valor);
        inv.setTipo(tipo);
        inv.setTipoRentabilidade(TipoRentabilidade.PRE);
        inv.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        inv.setIndice(Indice.SELIC);
        inv.setLiquidez(0);
        inv.setFgc(true);
        inv.setMinimoDiasInvestimento(90);
        return inv;
    }
}