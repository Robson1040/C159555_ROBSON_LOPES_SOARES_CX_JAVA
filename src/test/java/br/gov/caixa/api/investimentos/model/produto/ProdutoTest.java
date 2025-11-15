package br.gov.caixa.api.investimentos.model.produto;

import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.simulacao.TipoRenda;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoTest {

    @Test
    void testConstrutores() {
        Produto p1 = new Produto();
        assertEquals(0, p1.getPontuacao());

        Produto p2 = new Produto(
                "Produto Teste",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                BigDecimal.valueOf(10.0),
                PeriodoRentabilidade.AO_ANO,
                Indice.SELIC,
                30,
                10,
                true
        );

        assertEquals("Produto Teste", p2.getNome());
        assertEquals(TipoProduto.CDB, p2.getTipo());
        assertEquals(TipoRentabilidade.POS, p2.getTipoRentabilidade());
        assertEquals(BigDecimal.valueOf(10.0), p2.getRentabilidade());
        assertEquals(PeriodoRentabilidade.AO_ANO, p2.getPeriodoRentabilidade());
        assertEquals(Indice.SELIC, p2.getIndice());
        assertEquals(30, p2.getLiquidez());
        assertEquals(10, p2.getMinimoDiasInvestimento());
        assertTrue(p2.getFgc());
        assertEquals(0, p2.getPontuacao());
    }

    @Test
    void testGettersSetters() {
        Produto p = new Produto();
        p.setNome("Novo Produto");
        p.setTipo(TipoProduto.LCI);
        p.setTipoRentabilidade(TipoRentabilidade.PRE);
        p.setRentabilidade(BigDecimal.valueOf(5.5));
        p.setPeriodoRentabilidade(PeriodoRentabilidade.PERIODO_TOTAL);
        p.setIndice(Indice.IPCA);
        p.setLiquidez(15);
        p.setMinimoDiasInvestimento(5);
        p.setFgc(false);
        p.setPontuacao(10);
        p.setId(99L);

        assertEquals("Novo Produto", p.getNome());
        assertEquals(TipoProduto.LCI, p.getTipo());
        assertEquals(TipoRentabilidade.PRE, p.getTipoRentabilidade());
        assertEquals(BigDecimal.valueOf(5.5), p.getRentabilidade());
        assertEquals(PeriodoRentabilidade.PERIODO_TOTAL, p.getPeriodoRentabilidade());
        assertEquals(Indice.IPCA, p.getIndice());
        assertEquals(15, p.getLiquidez());
        assertEquals(5, p.getMinimoDiasInvestimento());
        assertFalse(p.getFgc());
        assertEquals(10, p.getPontuacao());
        assertEquals(99L, p.getId());
    }

    @Test
    void testGetRisco() {
        // Garantido pelo FGC
        Produto p1 = new Produto();
        p1.setFgc(true);
        assertEquals(NivelRisco.BAIXO, p1.getRisco());

        // Não garantido pelo FGC, RENDA_FIXA
        Produto p2 = new Produto();
        p2.setFgc(false);
        p2.setTipo(TipoProduto.CRI);
        assertEquals(NivelRisco.MEDIO, p2.getRisco());

        // Não garantido pelo FGC, RENDA_VARIAVEL
        Produto p3 = new Produto();
        p3.setFgc(false);
        p3.setTipo(TipoProduto.ACAO);
        assertEquals(NivelRisco.ALTO, p3.getRisco());

        // Não garantido pelo FGC, tipo nulo
        Produto p4 = new Produto();
        p4.setFgc(false);
        p4.setTipo(null);
        assertNull(p4.getRisco());
    }

    @Test
    void testPontuacaoPadrao() {
        Produto p = new Produto();
        assertEquals(0, p.getPontuacao());
    }
}