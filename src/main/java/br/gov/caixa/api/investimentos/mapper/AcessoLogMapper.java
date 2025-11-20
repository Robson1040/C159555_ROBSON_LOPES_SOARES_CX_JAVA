package br.gov.caixa.api.investimentos.mapper;

import br.gov.caixa.api.investimentos.dto.telemetria.AcessoLogDTO;
import br.gov.caixa.api.investimentos.model.telemetria.AcessoLog;

import java.util.List;
import java.util.stream.Collectors;

public class AcessoLogMapper {
    public static AcessoLogDTO toDTO(AcessoLog log) {
        return new AcessoLogDTO(
                log.getId(), log.getUsuarioId(), log.getEndpoint(),
                log.getMetodoHttp(), log.getUriCompleta(), log.getIpOrigem(),
                log.getCorpoRequisicao(), log.getStatusCode(), log.getCorpoResposta(),
                log.getTempoExecucaoMs(), log.getDataAcesso(), log.getUserAgent(),
                log.getErroMessage()
        );
    }

    public static List<AcessoLogDTO> toDTO(List<AcessoLog> logs) {
        return logs.stream()
                .map(AcessoLogMapper::toDTO)
                .collect(Collectors.toList());
    }
}
