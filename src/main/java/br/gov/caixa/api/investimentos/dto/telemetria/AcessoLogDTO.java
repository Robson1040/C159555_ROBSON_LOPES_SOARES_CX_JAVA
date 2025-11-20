package br.gov.caixa.api.investimentos.dto.telemetria;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public record AcessoLogDTO(
        Long id,
        Long usuarioId,
        String endpoint,
        String metodoHttp,
        String uriCompleta,
        String ipOrigem,

        @JsonIgnore
        String corpoRequisicao,

        Integer statusCode,

        @JsonIgnore
        String corpoResposta,

        Long tempoExecucaoMs,
        LocalDateTime dataAcesso,
        String userAgent,
        String erroMessage
) {
}

