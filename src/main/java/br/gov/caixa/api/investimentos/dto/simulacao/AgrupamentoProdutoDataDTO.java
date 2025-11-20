package br.gov.caixa.api.investimentos.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AgrupamentoProdutoDataDTO(
        @JsonProperty("produto")
        String produto,

        @JsonProperty("data")
        LocalDate data,

        @JsonProperty("quantidadeSimulacoes")
        Long quantidadeSimulacoes,

        @JsonProperty("mediaValorInvestido")
        BigDecimal mediaValorInvestido,

        @JsonProperty("mediaValorFinal")
        BigDecimal mediaValorFinal
) {
}