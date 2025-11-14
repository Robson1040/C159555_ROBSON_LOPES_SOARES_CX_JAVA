package org.example.model.telemetria;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetria_metrica")
public class TelemetriaMetrica extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    public Long id;

    @Column(name = "endpoint", nullable = false, length = 200)
    public String endpoint;
    
    @Column(name = "contador_execucoes", nullable = false)
    public Long contadorExecucoes;
    
    @Column(name = "tempo_medio_resposta", nullable = false)
    public Double tempoMedioResposta;
    
    @Column(name = "tempo_total_execucao", nullable = false)
    public Double tempoTotalExecucao;
    
    @Column(name = "data_criacao", nullable = false)
    public LocalDateTime dataCriacao;
    
    @Column(name = "ultima_atualizacao", nullable = false)
    public LocalDateTime ultimaAtualizacao;
    
    public TelemetriaMetrica() {
        this.dataCriacao = LocalDateTime.now();
        this.ultimaAtualizacao = LocalDateTime.now();
    }
    
    public TelemetriaMetrica(String endpoint) {
        this();
        this.endpoint = endpoint;
        this.contadorExecucoes = 0L;
        this.tempoMedioResposta = 0.0;
        this.tempoTotalExecucao = 0.0;
    }
    
    public void incrementarContador() {
        this.contadorExecucoes++;
        this.ultimaAtualizacao = LocalDateTime.now();
    }
    
    public void adicionarTempoExecucao(long tempoExecucaoMs) {
        this.tempoTotalExecucao += tempoExecucaoMs;
        this.tempoMedioResposta = this.tempoTotalExecucao / this.contadorExecucoes;
        this.ultimaAtualizacao = LocalDateTime.now();
    }
    
    public static TelemetriaMetrica findByEndpoint(String endpoint) {
        return find("endpoint", endpoint).firstResult();
    }
    
    @Override
    public String toString() {
        return String.format("TelemetriaMetrica{id=%d, endpoint='%s', execucoes=%d, tempoMedio=%.2f}", 
                id, endpoint, contadorExecucoes, tempoMedioResposta);
    }
}