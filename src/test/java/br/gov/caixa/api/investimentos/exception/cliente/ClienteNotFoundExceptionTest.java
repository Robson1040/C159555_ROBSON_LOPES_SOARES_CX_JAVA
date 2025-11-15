package br.gov.caixa.api.investimentos.exception.cliente;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteNotFoundExceptionTest {

    @Test
    void criarExcecao_comMensagem() {
        String mensagem = "Cliente não encontrado";
        ClienteNotFoundException ex = new ClienteNotFoundException(mensagem);

        assertEquals(mensagem, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void criarExcecao_comMensagemECausa() {
        String mensagem = "Erro ao buscar cliente";
        Throwable causa = new RuntimeException("Causa original");
        ClienteNotFoundException ex = new ClienteNotFoundException(mensagem, causa);

        assertEquals(mensagem, ex.getMessage());
        assertEquals(causa, ex.getCause());
    }
}