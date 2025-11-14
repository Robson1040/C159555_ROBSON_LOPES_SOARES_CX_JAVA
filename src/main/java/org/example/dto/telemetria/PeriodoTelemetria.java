package org.example.dto.telemetria;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class PeriodoTelemetria {
    
    @JsonProperty("inicio")
    private String inicio;
    
    @JsonProperty("fim")
    private String fim;

    public PeriodoTelemetria() {
    }

    public PeriodoTelemetria(String inicio, String fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public PeriodoTelemetria(LocalDate inicio, LocalDate fim) {
        this.inicio = inicio.toString();
        this.fim = fim.toString();
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFim() {
        return fim;
    }

    public void setFim(String fim) {
        this.fim = fim;
    }
}