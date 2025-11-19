package br.gov.caixa.api.investimentos.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;

import java.time.LocalDateTime;


public record SimulacaoResponse(
        @JsonProperty("produtoValidado")
        ProdutoResponse produtoValidado,

        @JsonProperty("resultadoSimulacao")
        ResultadoSimulacao resultadoSimulacao,

        @JsonProperty("dataSimulacao")
        LocalDateTime dataSimulacao,

        @JsonProperty("clienteId")
        Long clienteId,

        @JsonProperty("simulacaoId")
        Long simulacaoId
) {
    
    public SimulacaoResponse(ProdutoResponse produtoValidado, ResultadoSimulacao resultadoSimulacao, 
                            LocalDateTime dataSimulacao, Long clienteId) {
        this(produtoValidado, resultadoSimulacao, dataSimulacao, clienteId, null);
    }
}