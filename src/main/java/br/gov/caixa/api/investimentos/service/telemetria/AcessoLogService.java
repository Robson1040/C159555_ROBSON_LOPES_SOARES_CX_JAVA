package br.gov.caixa.api.investimentos.service.telemetria;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import br.gov.caixa.api.investimentos.model.telemetria.AcessoLog;
import br.gov.caixa.api.investimentos.repository.telemetria.AcessoLogRepository;
import br.gov.caixa.api.investimentos.dto.telemetria.AcessoLogDTO;
import br.gov.caixa.api.investimentos.mapper.AcessoLogMapper;
import br.gov.caixa.api.investimentos.dto.telemetria.EstatisticasAcessoDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço para gerenciar logs de acesso aos endpoints
 */
@ApplicationScoped
public class AcessoLogService {

    @Inject
    AcessoLogRepository acessoLogRepository;

    /**
     * Registra um novo acesso ao log
     */
    @Transactional
    public AcessoLog registrarAcesso(Long usuarioId, String endpoint,
                                      String metodoHttp, String uriCompleta, String ipOrigem,
                                      String corpoRequisicao, Integer statusCode, String corpoResposta,
                                      Long tempoExecucaoMs, String userAgent,
                                      String erroMessage, String erroStacktrace) {
        try {
            AcessoLog log = new AcessoLog();
            log.setUsuarioId(usuarioId);
            log.setEndpoint(endpoint);
            log.setMetodoHttp(metodoHttp);
            log.setUriCompleta(uriCompleta);
            log.setIpOrigem(ipOrigem);
            log.setCorpoRequisicao(truncarSeNecessario(corpoRequisicao, 10000));
            log.setStatusCode(statusCode);
            log.setCorpoResposta(truncarSeNecessario(corpoResposta, 10000));
            log.setTempoExecucaoMs(tempoExecucaoMs);
            log.setUserAgent(userAgent);
            log.setErroMessage(erroMessage);
            log.setErroStacktrace(truncarSeNecessario(erroStacktrace, 5000));
            log.setDataAcesso(LocalDateTime.now());

            acessoLogRepository.persist(log);
            return log;
        } catch (Exception e) {
            // Registra erro no log mas não lança exceção para não impactar request
            System.err.println("Erro ao registrar acesso log: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todos os logs de acesso
     */
    public List<AcessoLogDTO> listarTodosLogs() {
    List<AcessoLog> logs = acessoLogRepository.listarTodos();
    return AcessoLogMapper.toDTO(logs);
    }

    /**
     * Busca logs de um usuário específico
     */
    public List<AcessoLogDTO> buscarLogsPorUsuario(Long usuarioId) {
    List<AcessoLog> logs = acessoLogRepository.buscarPorUsuario(usuarioId);
    return AcessoLogMapper.toDTO(logs);
    }

    /**
     * Busca logs de um endpoint específico
     */
    public List<AcessoLogDTO> buscarLogsPorEndpoint(String endpoint) {
    List<AcessoLog> logs = acessoLogRepository.buscarPorEndpoint(endpoint);
    return AcessoLogMapper.toDTO(logs);
    }

    /**
     * Busca logs em um período
     */
    public List<AcessoLogDTO> buscarLogsPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
    List<AcessoLog> logs = acessoLogRepository.buscarPorPeriodo(dataInicio, dataFim);
    return AcessoLogMapper.toDTO(logs);
    }

    /**
     * Busca logs com erro
     */
    public List<AcessoLogDTO> buscarLogsComErro() {
    List<AcessoLog> logs = acessoLogRepository.buscarAcessosComErro();
    return AcessoLogMapper.toDTO(logs);
    }

    /**
     * Busca logs por status code
     */
    public List<AcessoLogDTO> buscarLogsPorStatusCode(Integer statusCode) {
    List<AcessoLog> logs = acessoLogRepository.buscarPorStatusCode(statusCode);
    return AcessoLogMapper.toDTO(logs);
    }

    /**
     * Busca logs com múltiplos filtros
     */
    public List<AcessoLogDTO> buscarLogsComFiltros(Long usuarioId, String endpoint,
                                                     LocalDateTime dataInicio, LocalDateTime dataFim) {
    List<AcessoLog> logs = acessoLogRepository.buscarComFiltros(usuarioId, endpoint, dataInicio, dataFim);
    return AcessoLogMapper.toDTO(logs);
    }

    /**
     * Conta total de logs
     */
    public long contarTodosLogs() {
        return acessoLogRepository.contarTodos();
    }

    /**
     * Conta logs de um usuário
     */
    public long contarLogsPorUsuario(Long usuarioId) {
        return acessoLogRepository.contarPorUsuario(usuarioId);
    }

    /**
     * Conta logs de um endpoint
     */
    public long contarLogsPorEndpoint(String endpoint) {
        return acessoLogRepository.contarPorEndpoint(endpoint);
    }

    /**
     * Conta logs com erro
     */
    public long contarLogsComErro() {
        return acessoLogRepository.contarAcessosComErro();
    }

    /**
     * Obtém estatísticas dos logs
     */
    public EstatisticasAcessoDTO obterEstatisticas() {
    long totalAcessos = acessoLogRepository.contarTodos();
    long acessosComErro = acessoLogRepository.contarAcessosComErro();
    long acessosSucesso = totalAcessos - acessosComErro;
    double taxaSucesso = totalAcessos > 0 ? (acessosSucesso * 100.0) / totalAcessos : 0.0;
    double taxaErro = totalAcessos > 0 ? (acessosComErro * 100.0) / totalAcessos : 0.0;
    return new EstatisticasAcessoDTO(totalAcessos, acessosSucesso, acessosComErro, taxaSucesso, taxaErro);
    }

    /**
     * Limpa logs antigos
     */
    @Transactional
    public void limparLogsAntigos(int diasRetencao) {
        acessoLogRepository.limparLogsAntigos(diasRetencao);
    }

    /**
     * Limpa todos os logs
     */
    @Transactional
    public void limparTodosLogs() {
        acessoLogRepository.limparTodosLogs();
    }

    // ...existing code...

    private String truncarSeNecessario(String texto, int maxLength) {
        if (texto == null) {
            return null;
        }
        if (texto.length() > maxLength) {
            return texto.substring(0, maxLength) + "... [truncado]";
        }
        return texto;
    }

    /**
     * DTO para estatísticas de acesso
     */
    // ...existing code...
}

