package br.gov.caixa.api.investimentos.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.validation.ValidRentabilidadeIndice;

import java.math.BigDecimal;

/**
 * DTO para requisições de criação/atualização de produtos
 */
@ValidRentabilidadeIndice
public record ProdutoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
        String nome,

        @NotNull(message = "Tipo do produto é obrigatório")
        TipoProduto tipo,

        @NotNull(message = "Tipo de rentabilidade é obrigatório")
        @JsonProperty("tipo_rentabilidade")
        TipoRentabilidade tipoRentabilidade,

        @NotNull(message = "Rentabilidade é obrigatória")
        @DecimalMin(value = "0.0", message = "Rentabilidade deve ser maior ou igual a zero")
        BigDecimal rentabilidade,

        @NotNull(message = "Período de rentabilidade é obrigatório")
        @JsonProperty("periodo_rentabilidade")
        PeriodoRentabilidade periodoRentabilidade,

        Indice indice,

        @NotNull(message = "Liquidez é obrigatória")
        @Min(value = -1, message = "Liquidez deve ser -1 (sem liquidez) ou >= 0")
        Integer liquidez,

        @NotNull(message = "Mínimo de dias de investimento é obrigatório")
        @Min(value = 0, message = "Mínimo de dias de investimento deve ser >= 0")
        @JsonProperty("minimo_dias_investimento")
        Integer minimoDiasInvestimento,

        @NotNull(message = "FGC é obrigatório")
        Boolean fgc
) {}
