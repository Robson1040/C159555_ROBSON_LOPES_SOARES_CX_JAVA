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
        // Produto com FGC = Baixo risco, independente de outros fatores
        Produto p1 = new Produto();
        p1.setFgc(true);
        p1.setRentabilidade(BigDecimal.valueOf(5.0));
        p1.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        p1.setLiquidez(0);
        p1.setMinimoDiasInvestimento(0);
        p1.setTipoRentabilidade(TipoRentabilidade.PRE);
        assertEquals(NivelRisco.BAIXO, p1.getRisco());

        // Produto sem FGC com baixa rentabilidade e boa liquidez = Médio risco
        // Pontuação: 30 (sem FGC) + 15 (8% rentabilidade) + 5 (liquidez 30 dias) + 5 (carência 60 dias) = 55 -> ALTO
        // Vamos ajustar para ficar em MEDIO (pontuação <= 50)
        Produto p2 = new Produto();
        p2.setFgc(false);
        p2.setTipo(TipoProduto.CRI);
        p2.setRentabilidade(BigDecimal.valueOf(5.0)); // Rentabilidade baixa = 0 pontos
        p2.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        p2.setLiquidez(0); // Liquidez imediata = 0 pontos  
        p2.setMinimoDiasInvestimento(0); // Sem carência = 0 pontos
        p2.setTipoRentabilidade(TipoRentabilidade.PRE); // Pré-fixado = 0 pontos
        // Pontuação total: 30 (sem FGC) = MEDIO
        assertEquals(NivelRisco.MEDIO, p2.getRisco());

        // Produto sem FGC com alta rentabilidade e baixa liquidez = Alto risco
        Produto p3 = new Produto();
        p3.setFgc(false);
        p3.setTipo(TipoProduto.ACAO);
        p3.setRentabilidade(BigDecimal.valueOf(25.0));
        p3.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        p3.setLiquidez(-1); // Sem liquidez
        p3.setMinimoDiasInvestimento(365);
        p3.setTipoRentabilidade(TipoRentabilidade.POS);
        assertEquals(NivelRisco.ALTO, p3.getRisco());

        // Produto com FGC tem pontuação baixa mas outros fatores podem elevar
        // FGC=true (0 pontos) + 15% rent (30 pontos) + liquidez 180 dias (10 pontos) + pós-fixado (10 pontos) = 50 -> MEDIO  
        Produto p4 = new Produto();
        p4.setFgc(true);
        p4.setRentabilidade(BigDecimal.valueOf(15.0));
        p4.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        p4.setLiquidez(180);
        p4.setMinimoDiasInvestimento(0);
        p4.setTipoRentabilidade(TipoRentabilidade.POS);
        assertEquals(NivelRisco.MEDIO, p4.getRisco());

        // Produto de médio risco - rentabilidade moderada sem FGC
        // Pontuação: 30 (sem FGC) + 15 (10% rentabilidade) + 10 (liquidez 90 dias) + 2 (carência 30 dias) = 57 -> ALTO
        // Vamos ajustar para MEDIO
        Produto p5 = new Produto();
        p5.setFgc(false);
        p5.setRentabilidade(BigDecimal.valueOf(8.0)); // 15 pontos
        p5.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        p5.setLiquidez(15); // 5 pontos
        p5.setMinimoDiasInvestimento(0); // 0 pontos
        p5.setTipoRentabilidade(TipoRentabilidade.PRE); // 0 pontos
        // Pontuação total: 30 + 15 + 5 = 50 -> MEDIO
        assertEquals(NivelRisco.MEDIO, p5.getRisco());
    }

    @Test
    void testPontuacaoPadrao() {
        Produto p = new Produto();
        assertEquals(0, p.getPontuacao());
    }
}