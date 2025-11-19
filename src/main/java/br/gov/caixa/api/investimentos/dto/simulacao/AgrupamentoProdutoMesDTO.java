package br.gov.caixa.api.investimentos.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.YearMonth;


public record AgrupamentoProdutoMesDTO(
        @JsonProperty("produto")
        String produto,
        
        @JsonProperty("mes")
        YearMonth mes,
        
        @JsonProperty("quantidadeSimulacoes")
        Long quantidadeSimulacoes,
        
        @JsonProperty("mediaValorInvestido")
        BigDecimal mediaValorInvestido,
        
        @JsonProperty("mediaValorFinal")
        BigDecimal mediaValorFinal
) {}