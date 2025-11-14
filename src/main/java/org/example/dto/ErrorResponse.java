package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO padronizado para respostas de erro da API
 * Centraliza o formato de erro em toda a aplicação
 */
public class ErrorResponse {
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("status")
    private Integer status;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("errors")
    private List<String> errors;

    // Construtor padrão
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Construtor simples
    public ErrorResponse(String message) {
        this();
        this.message = message;
    }

    // Construtor com status
    public ErrorResponse(String message, Integer status) {
        this(message);
        this.status = status;
    }

    // Construtor completo
    public ErrorResponse(String message, Integer status, String path) {
        this(message, status);
        this.path = path;
    }

    // Construtor para múltiplos erros (validação)
    public ErrorResponse(String message, Integer status, String path, List<String> errors) {
        this(message, status, path);
        this.errors = errors;
    }

    // Métodos estáticos para criar respostas comuns
    public static ErrorResponse badRequest(String message) {
        return new ErrorResponse(message, 400);
    }

    public static ErrorResponse badRequest(String message, String path) {
        return new ErrorResponse(message, 400, path);
    }

    public static ErrorResponse badRequest(String message, String path, List<String> errors) {
        return new ErrorResponse(message, 400, path, errors);
    }

    public static ErrorResponse notFound(String message) {
        return new ErrorResponse(message, 404);
    }

    public static ErrorResponse internalError(String message) {
        return new ErrorResponse(message, 500);
    }

    public static ErrorResponse validationError(String message, List<String> errors) {
        return new ErrorResponse(message, 400, null, errors);
    }

    // Getters e Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}