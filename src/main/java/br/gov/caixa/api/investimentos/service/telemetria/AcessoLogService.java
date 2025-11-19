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


@ApplicationScoped
public class AcessoLogService {

    @Inject
    AcessoLogRepository acessoLogRepository;

    
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
            
            System.err.println("Erro ao registrar acesso log: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    
    public List<AcessoLogDTO> listarTodosLogs() {
    List<AcessoLog> logs = acessoLogRepository.listarTodos();
    return AcessoLogMapper.toDTO(logs);
    }

    
    public List<AcessoLogDTO> buscarLogsPorUsuario(Long usuarioId) {
    List<AcessoLog> logs = acessoLogRepository.buscarPorUsuario(usuarioId);
    return AcessoLogMapper.toDTO(logs);
    }

    
    public List<AcessoLogDTO> buscarLogsPorEndpoint(String endpoint) {
    List<AcessoLog> logs = acessoLogRepository.buscarPorEndpoint(endpoint);
    return AcessoLogMapper.toDTO(logs);
    }

    
    public List<AcessoLogDTO> buscarLogsPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
    List<AcessoLog> logs = acessoLogRepository.buscarPorPeriodo(dataInicio, dataFim);
    return AcessoLogMapper.toDTO(logs);
    }

    
    public List<AcessoLogDTO> buscarLogsComErro() {
    List<AcessoLog> logs = acessoLogRepository.buscarAcessosComErro();
    return AcessoLogMapper.toDTO(logs);
    }

    
    public List<AcessoLogDTO> buscarLogsPorStatusCode(Integer statusCode) {
    List<AcessoLog> logs = acessoLogRepository.buscarPorStatusCode(statusCode);
    return AcessoLogMapper.toDTO(logs);
    }

    
    public List<AcessoLogDTO> buscarLogsComFiltros(Long usuarioId, String endpoint,
                                                     LocalDateTime dataInicio, LocalDateTime dataFim) {
    List<AcessoLog> logs = acessoLogRepository.buscarComFiltros(usuarioId, endpoint, dataInicio, dataFim);
    return AcessoLogMapper.toDTO(logs);
    }

    
    public long contarTodosLogs() {
        return acessoLogRepository.contarTodos();
    }

    
    public long contarLogsPorUsuario(Long usuarioId) {
        return acessoLogRepository.contarPorUsuario(usuarioId);
    }

    
    public long contarLogsPorEndpoint(String endpoint) {
        return acessoLogRepository.contarPorEndpoint(endpoint);
    }

    
    public long contarLogsComErro() {
        return acessoLogRepository.contarAcessosComErro();
    }

    
    public EstatisticasAcessoDTO obterEstatisticas() {
    long totalAcessos = acessoLogRepository.contarTodos();
    long acessosComErro = acessoLogRepository.contarAcessosComErro();
    long acessosSucesso = totalAcessos - acessosComErro;
    double taxaSucesso = totalAcessos > 0 ? (acessosSucesso * 100.0) / totalAcessos : 0.0;
    double taxaErro = totalAcessos > 0 ? (acessosComErro * 100.0) / totalAcessos : 0.0;
    return new EstatisticasAcessoDTO(totalAcessos, acessosSucesso, acessosComErro, taxaSucesso, taxaErro);
    }

    
    @Transactional
    public void limparLogsAntigos(int diasRetencao) {
        acessoLogRepository.limparLogsAntigos(diasRetencao);
    }

    
    @Transactional
    public void limparTodosLogs() {
        acessoLogRepository.limparTodosLogs();
    }

    

    private String truncarSeNecessario(String texto, int maxLength) {
        if (texto == null) {
            return null;
        }
        if (texto.length() > maxLength) {
            return texto.substring(0, maxLength) + "... [truncado]";
        }
        return texto;
    }

    
    
}

