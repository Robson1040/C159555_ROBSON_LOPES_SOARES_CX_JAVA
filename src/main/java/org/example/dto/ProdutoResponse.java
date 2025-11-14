package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.enums.Indice;
import org.example.enums.NivelRisco;
import org.example.enums.PeriodoRentabilidade;
import org.example.enums.TipoProduto;
import org.example.enums.TipoRentabilidade;

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