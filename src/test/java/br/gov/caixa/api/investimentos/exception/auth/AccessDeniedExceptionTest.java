package br.gov.caixa.api.investimentos.exception.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccessDeniedException - Testes unitários para construtores")
class AccessDeniedExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Given
        String mensagem = "Acesso negado ao recurso";

        // When
        AccessDeniedException exception = new AccessDeniedException(mensagem);

        // Then
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e causa")
    void deveCriarExcecaoComMensagemECausa() {
        // Given
        String mensagem = "Acesso negado ao recurso";
        Throwable causa = new IllegalStateException("Estado inválido");

        // When
        AccessDeniedException exception = new AccessDeniedException(mensagem, causa);

        // Then
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals(causa, exception.getCause());
        assertInstanceOf(IllegalStateException.class, exception.getCause());
    }

    @Test
    @DisplayName("Deve ser uma RuntimeException")
    void deveSerUmaRuntimeException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Teste");

        // When & Then
        assertInstanceOf(RuntimeException.class, exception);
        assertInstanceOf(Exception.class, exception);
        assertInstanceOf(Throwable.class, exception);
    }

    @Test
    @DisplayName("Deve funcionar com valores nulos")
    void deveFuncionarComValoresNulos() {
        // When & Then
        assertDoesNotThrow(() -> new AccessDeniedException(null));
        assertDoesNotThrow(() -> new AccessDeniedException(null, null));
        assertDoesNotThrow(() -> new AccessDeniedException("mensagem", null));
    }

    @Test
    @DisplayName("Deve ser lançável como exceção")
    void deveSerLancavelComoExcecao() {
        // Given
        String mensagem = "Usuário não autorizado";

        // When & Then
        RuntimeException exception = assertThrows(AccessDeniedException.class, () -> {
            throw new AccessDeniedException(mensagem);
        });

        assertEquals(mensagem, exception.getMessage());
    }
}