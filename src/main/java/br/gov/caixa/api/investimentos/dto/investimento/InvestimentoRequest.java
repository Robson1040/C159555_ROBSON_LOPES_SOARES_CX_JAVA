package br.gov.caixa.api.investimentos.dto.investimento;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import br.gov.caixa.api.investimentos.validation.ValidPrazo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para criação de um Investimento real (não apenas simulação)
 */
@ValidPrazo
public record InvestimentoRequest(
        @NotNull(message = "ID do cliente é obrigatório")
        @JsonProperty("clienteId")
        Long clienteId,

        @NotNull(message = "ID do produto é obrigatório")
        @JsonProperty("produtoId")
        Long produtoId,

        @NotNull(message = "Valor do investimento é obrigatório")
        @DecimalMin(value = "1.00", message = "Valor mínimo de investimento é R$ 1,00")
        @DecimalMax(value = "999999999.99", message = "Valor máximo de investimento é R$ 999.999.999,99")
        @JsonProperty("valor")
        BigDecimal valor,

        @JsonProperty("prazoMeses")
        @Min(value = 1, message = "Prazo em meses deve ser no mínimo 1")
        @Max(value = 600, message = "Prazo em meses deve ser no máximo 600 (50 anos)")
        Integer prazoMeses,

        @JsonProperty("prazoDias")
        @Min(value = 1, message = "Prazo em dias deve ser no mínimo 1")
        @Max(value = 18250, message = "Prazo em dias deve ser no máximo 18.250 (50 anos)")
        Integer prazoDias,

        @JsonProperty("prazoAnos")
        @Min(value = 1, message = "Prazo em anos deve ser no mínimo 1")
        @Max(value = 50, message = "Prazo em anos deve ser no máximo 50")
        Integer prazoAnos
) {
    public int getPrazoEmDias() {
        if (prazoDias != null && prazoDias > 0) return prazoDias;
        if (prazoMeses != null && prazoMeses > 0) return prazoMeses * 30;
        if (prazoAnos != null && prazoAnos > 0) return prazoAnos * 365;
        return 0;
    }

    public int getPrazoEmMeses() {
        if (prazoMeses != null && prazoMeses > 0) return prazoMeses;
        if (prazoDias != null && prazoDias > 0) return Math.max(1, prazoDias / 30);
        if (prazoAnos != null && prazoAnos > 0) return prazoAnos * 12;
        return 0;
    }
}