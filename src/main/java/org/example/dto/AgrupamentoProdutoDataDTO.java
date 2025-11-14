package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resposta do endpoint GET /simulacoes/por-produto-dia
 * Representa um agrupamento de simulações por produto e data
 */
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
) {}