package org.example.exception;

public class NenhumProdutoDisponivelException extends RuntimeException {

    public NenhumProdutoDisponivelException(String message) {
        super(message);
    }

    public NenhumProdutoDisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}