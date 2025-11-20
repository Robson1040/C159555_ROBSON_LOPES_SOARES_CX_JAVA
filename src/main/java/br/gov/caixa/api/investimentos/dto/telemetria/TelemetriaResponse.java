package br.gov.caixa.api.investimentos.dto.telemetria;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TelemetriaResponse {

    @JsonProperty("servicos")
    private List<ServicoTelemetria> servicos;

    @JsonProperty("periodo")
    private PeriodoTelemetria periodo;

    public TelemetriaResponse() {
    }

    public TelemetriaResponse(List<ServicoTelemetria> servicos, PeriodoTelemetria periodo) {
        this.servicos = servicos;
        this.periodo = periodo;
    }

    public List<ServicoTelemetria> getServicos() {
        return servicos;
    }

    public void setServicos(List<ServicoTelemetria> servicos) {
        this.servicos = servicos;
    }

    public PeriodoTelemetria getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoTelemetria periodo) {
        this.periodo = periodo;
    }
}