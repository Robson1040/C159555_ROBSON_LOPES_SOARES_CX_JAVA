package br.gov.caixa.api.investimentos.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        @JsonProperty("message")
        String message,

        @JsonProperty("timestamp")
        LocalDateTime timestamp,

        @JsonProperty("status")
        Integer status,

        @JsonProperty("path")
        @JsonIgnore
        String path,

        @JsonProperty("errors")
        List<String> errors
) {

    public ErrorResponse(String message) {
        this(message, LocalDateTime.now(), null, null, null);
    }

    public ErrorResponse(String message, Integer status) {
        this(message, LocalDateTime.now(), status, null, null);
    }

    public ErrorResponse(String message, Integer status, String path) {
        this(message, LocalDateTime.now(), status, path, null);
    }

    public static ErrorResponse badRequest(String message) {
        return new ErrorResponse(message, LocalDateTime.now(), 400, null, null);
    }

    public static ErrorResponse badRequest(String message, String path) {
        return new ErrorResponse(message, LocalDateTime.now(), 400, path, null);
    }

    public static ErrorResponse badRequest(String message, String path, List<String> errors) {
        return new ErrorResponse(message, LocalDateTime.now(), 400, path, errors);
    }

    public static ErrorResponse notFound(String message) {
        return new ErrorResponse(message, LocalDateTime.now(), 404, null, null);
    }

    public static ErrorResponse internalError(String message) {
        return new ErrorResponse(message, LocalDateTime.now(), 500, null, null);
    }

    public static ErrorResponse validationError(String message, List<String> errors) {
        return new ErrorResponse(message, LocalDateTime.now(), 400, null, errors);
    }

    public static ErrorResponse unauthorized(String message) {
        return new ErrorResponse(message, LocalDateTime.now(), 401, null, null);
    }

    public static ErrorResponse forbidden(String message) {
        return new ErrorResponse(message, LocalDateTime.now(), 403, null, null);
    }
}