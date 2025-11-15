package br.gov.caixa.api.investimentos.enums.produto;

import br.gov.caixa.api.investimentos.enums.simulacao.TipoRenda;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoProdutoTest {

    @Test
    void shouldContainAllEnumValues() {
        TipoProduto[] expected = {
                TipoProduto.CDB,
                TipoProduto.LCI,
                TipoProduto.LCA,
                TipoProduto.TESOURO_DIRETO,
                TipoProduto.POUPANCA,
                TipoProduto.DEBENTURE,
                TipoProduto.CRI,
                TipoProduto.FUNDO,
                TipoProduto.FII,
                TipoProduto.ACAO,
                TipoProduto.ETF
        };
        assertArrayEquals(expected, TipoProduto.values());
    }

    @Test
    void shouldReturnCorrectEnumByName() {
        for (TipoProduto produto : TipoProduto.values()) {
            assertEquals(produto, TipoProduto.valueOf(produto.name()));
        }
    }

    @Test
    void shouldReturnCorrectDescricao() {
        assertEquals("Certificado de Depósito Bancário", TipoProduto.CDB.getDescricao());
        assertEquals("Letra de Crédito Imobiliário", TipoProduto.LCI.getDescricao());
        assertEquals("Letra de Crédito do Agronegócio", TipoProduto.LCA.getDescricao());
        assertEquals("Tesouro Direto", TipoProduto.TESOURO_DIRETO.getDescricao());
        assertEquals("Poupança", TipoProduto.POUPANCA.getDescricao());
        assertEquals("Debênture", TipoProduto.DEBENTURE.getDescricao());
        assertEquals("Certificado de Recebíveis Imobiliários", TipoProduto.CRI.getDescricao());
        assertEquals("Fundo de Investimento", TipoProduto.FUNDO.getDescricao());
        assertEquals("Fundo de Investimento Imobiliário", TipoProduto.FII.getDescricao());
        assertEquals("Ação", TipoProduto.ACAO.getDescricao());
        assertEquals("ETF", TipoProduto.ETF.getDescricao());
    }

    @Test
    void shouldReturnCorrectTipoRenda() {
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.CDB.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.LCI.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.LCA.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.TESOURO_DIRETO.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.POUPANCA.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.DEBENTURE.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.CRI.getTipoRenda());
        assertEquals(TipoRenda.RENDA_VARIAVEL, TipoProduto.FUNDO.getTipoRenda());
        assertEquals(TipoRenda.RENDA_VARIAVEL, TipoProduto.FII.getTipoRenda());
        assertEquals(TipoRenda.RENDA_VARIAVEL, TipoProduto.ACAO.getTipoRenda());
        assertEquals(TipoRenda.RENDA_VARIAVEL, TipoProduto.ETF.getTipoRenda());
    }

    @Test
    void shouldReturnCorrectValor() {
        assertEquals(1, TipoProduto.CDB.getValor());
        assertEquals(2, TipoProduto.LCI.getValor());
        assertEquals(3, TipoProduto.LCA.getValor());
        assertEquals(4, TipoProduto.TESOURO_DIRETO.getValor());
        assertEquals(5, TipoProduto.POUPANCA.getValor());
        assertEquals(6, TipoProduto.DEBENTURE.getValor());
        assertEquals(7, TipoProduto.CRI.getValor());
        assertEquals(8, TipoProduto.FUNDO.getValor());
        assertEquals(9, TipoProduto.FII.getValor());
        assertEquals(10, TipoProduto.ACAO.getValor());
        assertEquals(11, TipoProduto.ETF.getValor());
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> TipoProduto.valueOf("INVALIDO"));
    }
}