package br.gov.caixa.api.investimentos.exception.produto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProdutoNotFoundException - Testes unitários para construtores")
class ProdutoNotFoundExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Given
        String mensagem = "Produto não encontrado";

        // When
        ProdutoNotFoundException exception = new ProdutoNotFoundException(mensagem);

        // Then
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e causa")
    void deveCriarExcecaoComMensagemECausa() {
        // Given
        String mensagem = "Produto não encontrado";
        Throwable causa = new IllegalArgumentException("ID inválido");

        // When
        ProdutoNotFoundException exception = new ProdutoNotFoundException(mensagem, causa);

        // Then
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals(causa, exception.getCause());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    @DisplayName("Deve ser uma RuntimeException")
    void deveSerUmaRuntimeException() {
        // Given
        ProdutoNotFoundException exception = new ProdutoNotFoundException("Teste");

        // When & Then
        assertInstanceOf(RuntimeException.class, exception);
        assertInstanceOf(Exception.class, exception);
        assertInstanceOf(Throwable.class, exception);
    }

    @Test
    @DisplayName("Deve funcionar com valores nulos")
    void deveFuncionarComValoresNulos() {
        // When & Then
        assertDoesNotThrow(() -> new ProdutoNotFoundException(null));
        assertDoesNotThrow(() -> new ProdutoNotFoundException(null, null));
        assertDoesNotThrow(() -> new ProdutoNotFoundException("mensagem", null));
    }

    @Test
    @DisplayName("Deve ser lançável como exceção")
    void deveSerLancavelComoExcecao() {
        // Given
        String mensagem = "Produto com ID 123 não encontrado";

        // When & Then
        RuntimeException exception = assertThrows(ProdutoNotFoundException.class, () -> {
            throw new ProdutoNotFoundException(mensagem);
        });

        assertEquals(mensagem, exception.getMessage());
    }
}