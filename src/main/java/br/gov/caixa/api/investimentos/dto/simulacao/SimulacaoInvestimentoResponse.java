package br.gov.caixa.api.investimentos.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record SimulacaoInvestimentoResponse(
        @JsonProperty("id")
        Long id,

        @JsonProperty("produto_id")
        Long produtoId,

        @JsonProperty("clienteId")
        Long clienteId,

        @JsonProperty("produto")
        String produto,

        @JsonProperty("valorInvestido")
        BigDecimal valorInvestido,

        @JsonProperty("valorFinal")
        BigDecimal valorFinal,

        @JsonProperty("prazoMeses")
        Integer prazoMeses,

        @JsonProperty("prazoDias")
        Integer prazoDias,

        @JsonProperty("prazoAnos")
        Integer prazoAnos,

        @JsonProperty("dataSimulacao")
        LocalDateTime dataSimulacao,

        @JsonProperty("rentabilidadeEfetiva")
        BigDecimal rentabilidadeEfetiva,

        @JsonProperty("rendimento")
        BigDecimal rendimento,

        @JsonProperty("valorSimulado")
        Boolean valorSimulado,

        @JsonProperty("cenarioSimulacao")
        String cenarioSimulacao
) {}
