package br.gov.caixa.api.investimentos.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Year;

/**
 * DTO para resposta do endpoint GET /simulacoes/por-produto-ano
 * Representa um agrupamento de simulações por produto e ano
 */
public record AgrupamentoProdutoAnoDTO(
        @JsonProperty("produto")
        String produto,
        
        @JsonProperty("ano")
        Year ano,
        
        @JsonProperty("quantidadeSimulacoes")
        Long quantidadeSimulacoes,
        
        @JsonProperty("mediaValorInvestido")
        BigDecimal mediaValorInvestido,
        
        @JsonProperty("mediaValorFinal")
        BigDecimal mediaValorFinal
) {}