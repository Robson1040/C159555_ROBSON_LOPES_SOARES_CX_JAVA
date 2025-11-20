package br.gov.caixa.api.investimentos.repository.telemetria;

import br.gov.caixa.api.investimentos.model.telemetria.TelemetriaMetrica;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class TelemetriaMetricaRepository implements ITelemetriaMetricaRepository {

    /**
     * Busca uma métrica por endpoint
     */
    public TelemetriaMetrica findByEndpoint(String endpoint) {
        return find("endpoint", endpoint).firstResult();
    }

    /**
     * Busca ou cria uma nova métrica para um endpoint
     */
    @Transactional
    public TelemetriaMetrica findOrCreateByEndpoint(String endpoint) {
        TelemetriaMetrica metrica = findByEndpoint(endpoint);
        if (metrica == null) {
            metrica = new TelemetriaMetrica(endpoint);
            persist(metrica);
        }
        return metrica;
    }

    /**
     * Incrementa contador de execuções para um endpoint
     */
    @Transactional
    public void incrementarContador(String endpoint) {
        TelemetriaMetrica metrica = findOrCreateByEndpoint(endpoint);
        metrica.incrementarContador();
        persist(metrica);
    }

    /**
     * Registra tempo de execução para um endpoint (SEM incrementar contador)
     * Usado quando o contador já foi incrementado separadamente
     */
    @Transactional
    public void adicionarTempoExecucao(String endpoint, long tempoExecucaoMs) {
        TelemetriaMetrica metrica = findOrCreateByEndpoint(endpoint);
        metrica.adicionarTempoExecucao(tempoExecucaoMs);
        persist(metrica);
    }

    /**
     * Registra tempo de execução para um endpoint E incrementa contador
     * Usado para operações completas em uma única transação
     */
    @Transactional
    public void registrarTempoExecucao(String endpoint, long tempoExecucaoMs) {
        TelemetriaMetrica metrica = findOrCreateByEndpoint(endpoint);
        metrica.incrementarContador();
        metrica.adicionarTempoExecucao(tempoExecucaoMs);
        persist(metrica);
    }

    /**
     * Obtém todos os endpoints com métricas
     */
    public Set<String> obterEndpointsComMetricas() {
        return listAll().stream()
                .map(TelemetriaMetrica::getEndpoint)
                .collect(Collectors.toSet());
    }

    /**
     * Obtém contador de execuções de um endpoint
     */
    public Long obterContadorExecucoes(String endpoint) {
        TelemetriaMetrica metrica = findByEndpoint(endpoint);
        return metrica != null ? metrica.getContadorExecucoes() : 0L;
    }

    /**
     * Obtém tempo médio de resposta de um endpoint
     */
    public Double obterTempoMedioResposta(String endpoint) {
        TelemetriaMetrica metrica = findByEndpoint(endpoint);
        return metrica != null ? metrica.getTempoMedioResposta() : 0.0;
    }

    /**
     * Lista métricas por data de criação
     */
    public List<TelemetriaMetrica> listarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return find("dataCriacao >= ?1 and dataCriacao <= ?2", dataInicio, dataFim).list();
    }

    /**
     * Lista as métricas mais acessadas (ordenadas por contador de execuções)
     */
    public List<TelemetriaMetrica> listarMaisAcessadas(int limite) {
        return find("order by contadorExecucoes desc").page(0, limite).list();
    }

    /**
     * Obtém a data da primeira métrica registrada (início da coleta)
     */
    public LocalDateTime obterDataInicio() {
        TelemetriaMetrica primeiraMetrica = find("order by dataCriacao asc").firstResult();
        return primeiraMetrica != null ? primeiraMetrica.getDataCriacao() : null;
    }

    /**
     * Obtém a data da última atualização de métrica (fim da coleta)
     */
    public LocalDateTime obterDataFim() {
        TelemetriaMetrica ultimaMetrica = find("order by ultimaAtualizacao desc").firstResult();
        return ultimaMetrica != null ? ultimaMetrica.getUltimaAtualizacao() : null;
    }

    /**
     * Remove todas as métricas (para limpeza/reset)
     */
    @Transactional
    public void limparTodasMetricas() {
        deleteAll();
    }
}