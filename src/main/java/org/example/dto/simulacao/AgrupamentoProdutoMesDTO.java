package org.example.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * DTO para resposta do endpoint GET /simulacoes/por-produto-mes
 * Representa um agrupamento de simulações por produto e mês
 */
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