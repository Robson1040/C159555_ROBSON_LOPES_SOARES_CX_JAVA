
package br.gov.caixa.api.investimentos.dto.telemetria;

import java.time.LocalDateTime;

/**
 * DTO para exibição de logs de acesso
 */
public record AcessoLogDTO(
    Long id,
    Long usuarioId,
    String endpoint,
    String metodoHttp,
    String uriCompleta,
    String ipOrigem,
    String corpoRequisicao,
    Integer statusCode,
    String corpoResposta,
    Long tempoExecucaoMs,
    LocalDateTime dataAcesso,
    String userAgent,
    String erroMessage
) {}

