package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * DTO para o resultado da simulação financeira
 */
public record ResultadoSimulacao(
        @JsonProperty("valorFinal")
        BigDecimal valorFinal,

        @JsonProperty("rentabilidadeEfetiva")
        BigDecimal rentabilidadeEfetiva,

        @JsonProperty("prazoMeses")
        Integer prazoMeses,

        @JsonProperty("prazoDias")
        Integer prazoDias,

        @JsonProperty("prazoAnos")
        Integer prazoAnos,

        @JsonProperty("valorInvestido")
        BigDecimal valorInvestido,

        @JsonProperty("rendimento")
        BigDecimal rendimento,

        @JsonProperty("valorSimulado")
        Boolean valorSimulado,

        @JsonProperty("cenarioSimulacao")
        String cenarioSimulacao
) {}