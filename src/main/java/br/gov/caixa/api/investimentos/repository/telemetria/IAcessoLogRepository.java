package br.gov.caixa.api.investimentos.repository.telemetria;

import br.gov.caixa.api.investimentos.model.telemetria.AcessoLog;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface para operações de banco de dados da entidade AcessoLog
 */
public interface IAcessoLogRepository extends PanacheRepository<AcessoLog> {

    List<AcessoLog> listarTodos();

    List<AcessoLog> buscarPorUsuario(Long usuarioId);

    List<AcessoLog> buscarPorEndpoint(String endpoint);

    List<AcessoLog> buscarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<AcessoLog> buscarAcessosComErro();

    List<AcessoLog> buscarPorStatusCode(Integer statusCode);

    List<AcessoLog> buscarComFiltros(Long usuarioId, String endpoint,
                                     LocalDateTime dataInicio, LocalDateTime dataFim);

    long contarTodos();

    long contarPorUsuario(Long usuarioId);

    long contarPorEndpoint(String endpoint);

    long contarAcessosComErro();

    void limparLogsAntigos(int diasRetencao);

    void limparTodosLogs();
}
