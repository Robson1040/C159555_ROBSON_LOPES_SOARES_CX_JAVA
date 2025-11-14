package org.example.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.model.simulacao.SimulacaoInvestimento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para resposta de simulações persistidas
 * Usado nos endpoints de consulta de histórico
 */
public record SimulacaoInvestimentoResponse(
        @JsonProperty("id")
        Long id,

        @JsonProperty("clienteId")
        Long clienteId,

        @JsonProperty("produto")
        String produto,

        @JsonProperty("valorInvestido")
        BigDecimal valorInvestido,

        @JsonProperty("valorFinal")
        BigDecimal valorFinal,

        @JsonProperty("prazoMeses")
        Integer prazoMeses,

        @JsonProperty("prazoDias")
        Integer prazoDias,

        @JsonProperty("prazoAnos")
        Integer prazoAnos,

        @JsonProperty("dataSimulacao")
        LocalDateTime dataSimulacao,

        @JsonProperty("rentabilidadeEfetiva")
        BigDecimal rentabilidadeEfetiva,

        @JsonProperty("rendimento")
        BigDecimal rendimento,

        @JsonProperty("valorSimulado")
        Boolean valorSimulado,

        @JsonProperty("cenarioSimulacao")
        String cenarioSimulacao
) {
    // Construtor a partir da entidade
    public SimulacaoInvestimentoResponse(SimulacaoInvestimento entidade) {
        this(entidade.id, entidade.clienteId, entidade.produto, entidade.valorInvestido,
             entidade.valorFinal, entidade.prazoMeses, entidade.prazoDias, entidade.prazoAnos,
             entidade.dataSimulacao, entidade.rentabilidadeEfetiva, entidade.rendimento,
             entidade.valorSimulado, entidade.cenarioSimulacao);
    }

    // Método utilitário para converter lista
    public static List<SimulacaoInvestimentoResponse> fromList(List<SimulacaoInvestimento> entidades) {
        return entidades.stream()
                .map(SimulacaoInvestimentoResponse::new)
                .collect(Collectors.toList());
    }
}
