package br.gov.caixa.api.investimentos.repository.telemetria;

import br.gov.caixa.api.investimentos.model.telemetria.TelemetriaMetrica;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Interface para operações de banco de dados da entidade TelemetriaMetrica
 */
public interface ITelemetriaMetricaRepository extends PanacheRepository<TelemetriaMetrica> {

    TelemetriaMetrica findByEndpoint(String endpoint);

    TelemetriaMetrica findOrCreateByEndpoint(String endpoint);

    void incrementarContador(String endpoint);

    void adicionarTempoExecucao(String endpoint, long tempoExecucaoMs);

    void registrarTempoExecucao(String endpoint, long tempoExecucaoMs);

    Set<String> obterEndpointsComMetricas();

    Long obterContadorExecucoes(String endpoint);

    Double obterTempoMedioResposta(String endpoint);

    List<TelemetriaMetrica> listarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<TelemetriaMetrica> listarMaisAcessadas(int limite);

    LocalDateTime obterDataInicio();

    LocalDateTime obterDataFim();

    void limparTodasMetricas();
}

