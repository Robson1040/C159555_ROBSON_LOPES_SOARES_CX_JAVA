package br.gov.caixa.api.investimentos.exception.auth;

/**
 * Exceção lançada quando um usuário tenta acessar recursos
 * para os quais não possui autorização
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}