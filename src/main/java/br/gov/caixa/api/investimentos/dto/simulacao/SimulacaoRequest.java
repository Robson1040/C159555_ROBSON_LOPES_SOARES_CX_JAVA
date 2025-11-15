package br.gov.caixa.api.investimentos.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.validation.ValidPrazo;

import java.math.BigDecimal;

/**
 * DTO para simulação de investimento
 * Todos os campos de filtro são opcionais, exceto clienteId e valor
 */
@ValidPrazo
public record SimulacaoRequest(
        @NotNull(message = "ID do cliente é obrigatório")
        @JsonProperty("clienteId")
        Long clienteId,

        @JsonProperty("produto_id")
        Long produtoId,

        @NotNull(message = "Valor do investimento é obrigatório")
        @DecimalMin(value = "1.00", message = "Valor mínimo de investimento é R$ 1,00")
        @DecimalMax(value = "999999999.99", message = "Valor máximo de investimento é R$ 999.999.999,99")
        BigDecimal valor,

        // Prazo - apenas um dos três pode ser informado
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
        Integer prazoAnos,

        // Filtros opcionais do produto
        @JsonProperty("tipoProduto")
        TipoProduto tipoProduto,

        @JsonProperty("produto")
        String nome,

        @JsonProperty("tipo_rentabilidade")
        TipoRentabilidade tipoRentabilidade,

        Indice indice,

        @JsonProperty("liquidez")
        @NotNull(message = "Liquidez é obrigatória")
        @Min(value = -1, message = "Liquidez deve ser -1 (sem liquidez) ou >= 0")
        Integer liquidez,

        Boolean fgc
) {
    /**
     * Calcula o prazo em dias baseado no campo informado
     * @return prazo em dias
     */
    public int getPrazoEmDias() {
        if (prazoDias != null && prazoDias > 0) {
            return prazoDias;
        }
        if (prazoMeses != null && prazoMeses > 0) {
            return prazoMeses * 30; // Aproximação
        }
        if (prazoAnos != null && prazoAnos > 0) {
            return prazoAnos * 365; // Aproximação
        }
        return 365; // Default 1 ano
    }

    /**
     * Calcula o prazo em meses baseado no campo informado
     * @return prazo em meses
     */
    public int getPrazoEmMeses() {
        if (prazoMeses != null && prazoMeses > 0) {
            return prazoMeses;
        }
        if (prazoDias != null && prazoDias > 0) {
            return Math.max(1, prazoDias / 30); // Pelo menos 1 mês
        }
        if (prazoAnos != null && prazoAnos > 0) {
            return prazoAnos * 12;
        }
        return 12; // Default 1 ano
    }
}
