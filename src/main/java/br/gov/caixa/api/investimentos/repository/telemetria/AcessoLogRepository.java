package br.gov.caixa.api.investimentos.repository.telemetria;

import br.gov.caixa.api.investimentos.model.telemetria.AcessoLog;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AcessoLogRepository implements IAcessoLogRepository {

    /**
     * Busca todos os acessos ordenados por data descendente
     */
    public List<AcessoLog> listarTodos() {
        return listAll(Sort.by("dataAcesso").descending());
    }

    /**
     * Busca acessos de um usuário específico
     */
    public List<AcessoLog> buscarPorUsuario(Long usuarioId) {
        return list("usuarioId", Sort.by("dataAcesso").descending(), usuarioId);
    }

    /**
     * Busca acessos a um endpoint específico
     */
    public List<AcessoLog> buscarPorEndpoint(String endpoint) {
        return list("endpoint", Sort.by("dataAcesso").descending(), endpoint);
    }

    /**
     * Busca acessos em um período de tempo
     */
    public List<AcessoLog> buscarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return list("dataAcesso >= ?1 and dataAcesso <= ?2", Sort.by("dataAcesso").descending(),
                dataInicio, dataFim);
    }

    /**
     * Busca acessos com erro (status code >= 400)
     */
    public List<AcessoLog> buscarAcessosComErro() {
        return list("statusCode >= 400", Sort.by("dataAcesso").descending());
    }

    /**
     * Busca acessos por status code específico
     */
    public List<AcessoLog> buscarPorStatusCode(Integer statusCode) {
        return list("statusCode", Sort.by("dataAcesso").descending(), statusCode);
    }

    /**
     * Busca acessos com filtros múltiplos
     */
    public List<AcessoLog> buscarComFiltros(Long usuarioId, String endpoint,
                                            LocalDateTime dataInicio, LocalDateTime dataFim) {

        // Se nenhum filtro for fornecido, retorna todos
        if (usuarioId == null && (endpoint == null || endpoint.isEmpty()) &&
                (dataInicio == null && dataFim == null)) {
            return listAll(Sort.by("dataAcesso").descending());
        }

        // Construir a query dinamicamente
        StringBuilder query = new StringBuilder();

        if (usuarioId != null) {
            query.append("usuarioId = ?1 ");
        }
        if (endpoint != null && !endpoint.isEmpty()) {
            if (!query.isEmpty()) query.append("and ");
            query.append("endpoint = ?2 ");
        }
        if (dataInicio != null && dataFim != null) {
            if (!query.isEmpty()) query.append("and ");
            query.append("dataAcesso between ?3 and ?4 ");
        }

        if (query.isEmpty()) {
            return listAll(Sort.by("dataAcesso").descending());
        }

        // Executar query com parâmetros posicionais
        if (usuarioId != null && (endpoint == null || endpoint.isEmpty()) &&
                (dataInicio == null || dataFim == null)) {
            return list(query.toString(), Sort.by("dataAcesso").descending(), usuarioId);
        } else if ((usuarioId == null) && (endpoint != null && !endpoint.isEmpty()) &&
                (dataInicio == null || dataFim == null)) {
            return list(query.toString(), Sort.by("dataAcesso").descending(), endpoint);
        } else if ((usuarioId == null) && (endpoint == null || endpoint.isEmpty()) &&
                (dataInicio != null && dataFim != null)) {
            return list(query.toString(), Sort.by("dataAcesso").descending(), dataInicio, dataFim);
        } else if ((usuarioId != null) && (endpoint != null && !endpoint.isEmpty()) &&
                (dataInicio == null || dataFim == null)) {
            return list(query.toString(), Sort.by("dataAcesso").descending(), usuarioId, endpoint);
        } else if ((usuarioId != null) && (endpoint == null || endpoint.isEmpty()) &&
                (dataInicio != null && dataFim != null)) {
            return list(query.toString(), Sort.by("dataAcesso").descending(), usuarioId, dataInicio, dataFim);
        } else if ((usuarioId == null) && (endpoint != null && !endpoint.isEmpty()) &&
                (dataInicio != null && dataFim != null)) {
            return list(query.toString(), Sort.by("dataAcesso").descending(), endpoint, dataInicio, dataFim);
        } else {
            // Todos os filtros fornecidos
            return list(query.toString(), Sort.by("dataAcesso").descending(), usuarioId, endpoint, dataInicio, dataFim);
        }
    }

    /**
     * Conta total de acessos
     */
    public long contarTodos() {
        return count();
    }

    /**
     * Conta acessos de um usuário
     */
    public long contarPorUsuario(Long usuarioId) {
        return count("usuarioId", usuarioId);
    }

    /**
     * Conta acessos a um endpoint
     */
    public long contarPorEndpoint(String endpoint) {
        return count("endpoint", endpoint);
    }

    /**
     * Conta acessos com erro
     */
    public long contarAcessosComErro() {
        return count("statusCode >= 400");
    }

    /**
     * Limpa logs antigos (mais de N dias)
     */
    public void limparLogsAntigos(int diasRetencao) {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(diasRetencao);
        delete("dataAcesso < ?1", dataLimite);
    }

    /**
     * Limpa todos os logs
     */
    public void limparTodosLogs() {
        deleteAll();
    }
}
