package br.gov.caixa.api.investimentos.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;

import java.math.BigDecimal;

/**
 * DTO para respostas de produtos
 */
public record ProdutoResponse(
        Long id,
        String nome,
        TipoProduto tipo,

        @JsonProperty("tipo_rentabilidade")
        TipoRentabilidade tipoRentabilidade,

        BigDecimal rentabilidade,

        @JsonProperty("periodo_rentabilidade")
        PeriodoRentabilidade periodoRentabilidade,

        Indice indice,

        Integer liquidez,

        @JsonProperty("minimo_dias_investimento")
        Integer minimoDiasInvestimento,

        Boolean fgc,

        NivelRisco risco
) {}
