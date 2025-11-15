package br.gov.caixa.api.investimentos.exception.produto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NenhumProdutoDisponivelExceptionTest {

    @Test
    void criarExcecao_comMensagem() {
        String mensagem = "Nenhum produto disponível";
        NenhumProdutoDisponivelException ex = new NenhumProdutoDisponivelException(mensagem);

        assertEquals(mensagem, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void criarExcecao_comMensagemECausa() {
        String mensagem = "Erro ao buscar produtos";
        Throwable causa = new RuntimeException("Causa original");
        NenhumProdutoDisponivelException ex = new NenhumProdutoDisponivelException(mensagem, causa);

        assertEquals(mensagem, ex.getMessage());
        assertEquals(causa, ex.getCause());
    }
}