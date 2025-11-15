package br.gov.caixa.api.investimentos.dto.investimento;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de resposta após criação de Investimento
 */
public record InvestimentoResponse(
        Long id,
        @JsonProperty("clienteId") Long clienteId,
        @JsonProperty("produtoId") Long produtoId,
        @JsonProperty("valor") BigDecimal valor,
        Integer prazoMeses,
        Integer prazoDias,
        Integer prazoAnos,
        @JsonProperty("data") LocalDate data,
        @JsonProperty("tipo") TipoProduto tipo,
        @JsonProperty("tipo_rentabilidade") TipoRentabilidade tipoRentabilidade,
        @JsonProperty("rentabilidade") BigDecimal rentabilidade,
        @JsonProperty("periodo_rentabilidade") PeriodoRentabilidade periodoRentabilidade,
        @JsonProperty("indice") Indice indice,
        @JsonProperty("liquidez") Integer liquidez,
        @JsonProperty("minimo_dias_investimento") Integer minimoDiasInvestimento,
        @JsonProperty("fgc") Boolean fgc
) {}
