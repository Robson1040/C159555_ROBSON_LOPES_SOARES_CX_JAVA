package org.example.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.enums.simulacao.Indice;
import org.example.enums.produto.NivelRisco;
import org.example.enums.produto.PeriodoRentabilidade;
import org.example.enums.produto.TipoProduto;
import org.example.enums.produto.TipoRentabilidade;

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
