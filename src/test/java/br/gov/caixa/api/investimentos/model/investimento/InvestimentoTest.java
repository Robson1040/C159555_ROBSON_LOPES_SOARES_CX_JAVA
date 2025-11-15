package br.gov.caixa.api.investimentos.model.investimento;

import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.mapper.InvestimentoMapper;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvestimentoTest {

    private InvestimentoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InvestimentoMapper();
    }

    @Test
    void testGettersSetters() {
        Investimento inv = new Investimento();
        inv.setId(1L);
        inv.setClienteId(2L);
        inv.setProdutoId(3L);
        inv.setValor(BigDecimal.valueOf(1000));
        inv.setPrazoMeses(12);
        inv.setPrazoDias(360);
        inv.setPrazoAnos(1);
        inv.setData(LocalDate.now());
        inv.setTipo(TipoProduto.FUNDO);
        inv.setTipoRentabilidade(TipoRentabilidade.POS);
        inv.setRentabilidade(BigDecimal.valueOf(5.0));
        inv.setPeriodoRentabilidade(PeriodoRentabilidade.AO_DIA);
        inv.setIndice(Indice.SELIC);
        inv.setLiquidez(30);
        inv.setMinimoDiasInvestimento(10);
        inv.setFgc(true);

        assertEquals(1L, inv.getId());
        assertEquals(2L, inv.getClienteId());
        assertEquals(3L, inv.getProdutoId());
        assertEquals(BigDecimal.valueOf(1000), inv.getValor());
        assertEquals(12, inv.getPrazoMeses());
        assertEquals(360, inv.getPrazoDias());
        assertEquals(1, inv.getPrazoAnos());
        assertNotNull(inv.getData());
        assertEquals(TipoProduto.FUNDO, inv.getTipo());
        assertEquals(TipoRentabilidade.POS, inv.getTipoRentabilidade());
        assertEquals(BigDecimal.valueOf(5.0), inv.getRentabilidade());
        assertEquals(PeriodoRentabilidade.AO_DIA, inv.getPeriodoRentabilidade());
        assertEquals(Indice.SELIC, inv.getIndice());
        assertEquals(30, inv.getLiquidez());
        assertEquals(10, inv.getMinimoDiasInvestimento());
        assertTrue(inv.getFgc());
    }

    @Test
    void testFromMethod() {
        // Criação do request
        InvestimentoRequest req = new InvestimentoRequest(
                2L,
                3L,
                BigDecimal.valueOf(1000),
                12,
                360,
                1,
                LocalDate.now()
        );

        // Criação do produto
        Produto produto = new Produto(
                "Produto Teste",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                BigDecimal.valueOf(5.0),
                PeriodoRentabilidade.AO_MES,
                Indice.SELIC,
                30,
                10,
                true
        );

        Investimento inv = mapper.toEntity(req, produto);

        assertEquals(req.clienteId(), inv.getClienteId());
        assertEquals(req.produtoId(), inv.getProdutoId());
        assertEquals(req.valor(), inv.getValor());
        assertEquals(req.prazoMeses(), inv.getPrazoMeses());
        assertEquals(req.prazoDias(), inv.getPrazoDias());
        assertEquals(req.prazoAnos(), inv.getPrazoAnos());
        assertEquals(req.data(), inv.getData());

        // Campos copiados do produto
        assertEquals(produto.getTipo(), inv.getTipo());
        assertEquals(produto.getTipoRentabilidade(), inv.getTipoRentabilidade());
        assertEquals(produto.getRentabilidade(), inv.getRentabilidade());
        assertEquals(produto.getPeriodoRentabilidade(), inv.getPeriodoRentabilidade());
        assertEquals(produto.getIndice(), inv.getIndice());
        assertEquals(produto.getLiquidez(), inv.getLiquidez());
        assertEquals(produto.getMinimoDiasInvestimento(), inv.getMinimoDiasInvestimento());
        assertEquals(produto.getFgc(), inv.getFgc());
    }
}