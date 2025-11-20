package br.gov.caixa.api.investimentos.dto.telemetria;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServicoTelemetria {

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("contador_execucao")
    private long quantidadeChamadas;

    @JsonProperty("tempo_medio_resposta")
    private double mediaTempoRespostaMs;

    public ServicoTelemetria() {
    }

    public ServicoTelemetria(String nome, long quantidadeChamadas, double mediaTempoRespostaMs) {
        this.nome = nome;
        this.quantidadeChamadas = quantidadeChamadas;
        this.mediaTempoRespostaMs = mediaTempoRespostaMs;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getQuantidadeChamadas() {
        return quantidadeChamadas;
    }

    public void setQuantidadeChamadas(long quantidadeChamadas) {
        this.quantidadeChamadas = quantidadeChamadas;
    }

    public double getMediaTempoRespostaMs() {
        return mediaTempoRespostaMs;
    }

    public void setMediaTempoRespostaMs(double mediaTempoRespostaMs) {
        this.mediaTempoRespostaMs = mediaTempoRespostaMs;
    }
}