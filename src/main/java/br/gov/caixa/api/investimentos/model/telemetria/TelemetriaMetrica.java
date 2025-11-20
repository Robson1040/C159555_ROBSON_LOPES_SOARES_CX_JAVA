package br.gov.caixa.api.investimentos.model.telemetria;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetria_metrica")
public class TelemetriaMetrica extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @Column(name = "endpoint", nullable = false, length = 200)
    private String endpoint;

    @Column(name = "contador_execucoes", nullable = false)
    private Long contadorExecucoes;

    @Column(name = "tempo_medio_resposta", nullable = false)
    private Double tempoMedioResposta;

    @Column(name = "tempo_total_execucao", nullable = false)
    private Double tempoTotalExecucao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "ultima_atualizacao", nullable = false)
    private LocalDateTime ultimaAtualizacao;

    public TelemetriaMetrica() {
        this.setDataCriacao(LocalDateTime.now());
        this.setUltimaAtualizacao(LocalDateTime.now());
    }

    public TelemetriaMetrica(String endpoint) {
        this();
        this.setEndpoint(endpoint);
        this.setContadorExecucoes(0L);
        this.setTempoMedioResposta(0.0);
        this.setTempoTotalExecucao(0.0);
    }

    public void incrementarContador() {
        this.setContadorExecucoes(this.getContadorExecucoes() + 1);
        this.setUltimaAtualizacao(LocalDateTime.now());
    }

    public void adicionarTempoExecucao(long tempoExecucaoMs) {
        this.setTempoTotalExecucao(this.getTempoTotalExecucao() + tempoExecucaoMs);
        this.setTempoMedioResposta(this.getTempoTotalExecucao() / this.getContadorExecucoes());
        this.setUltimaAtualizacao(LocalDateTime.now());
    }

    public static TelemetriaMetrica findByEndpoint(String endpoint) {
        return find("endpoint", endpoint).firstResult();
    }

    @Override
    public String toString() {
        return String.format("TelemetriaMetrica{id=%d, endpoint='%s', execucoes=%d, tempoMedio=%.2f}",
                getId(), getEndpoint(), getContadorExecucoes(), getTempoMedioResposta());
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Double getTempoTotalExecucao() {
        return tempoTotalExecucao;
    }

    public void setTempoTotalExecucao(Double tempoTotalExecucao) {
        this.tempoTotalExecucao = tempoTotalExecucao;
    }

    public Double getTempoMedioResposta() {
        return tempoMedioResposta;
    }

    public void setTempoMedioResposta(Double tempoMedioResposta) {
        this.tempoMedioResposta = tempoMedioResposta;
    }

    public Long getContadorExecucoes() {
        return contadorExecucoes;
    }

    public void setContadorExecucoes(Long contadorExecucoes) {
        this.contadorExecucoes = contadorExecucoes;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}