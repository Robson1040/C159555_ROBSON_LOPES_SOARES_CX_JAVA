package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.model.Indice;
import org.example.model.NivelRisco;
import org.example.model.PeriodoRentabilidade;
import org.example.model.TipoProduto;
import org.example.model.TipoRentabilidade;

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