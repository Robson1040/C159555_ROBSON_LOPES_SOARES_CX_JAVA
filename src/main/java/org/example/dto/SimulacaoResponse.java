package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO para a resposta completa da simulação de investimento
 */
public class SimulacaoResponse {

    @JsonProperty("produtoValidado")
    private ProdutoResponse produtoValidado;

    @JsonProperty("resultadoSimulacao")
    private ResultadoSimulacao resultadoSimulacao;

    @JsonProperty("dataSimulacao")
    private LocalDateTime dataSimulacao;

    @JsonProperty("clienteId")
    private Long clienteId;

    @JsonProperty("simulacaoId")
    private Long simulacaoId;

    // Construtor padrão
    public SimulacaoResponse() {
    }

    // Construtor completo
    public SimulacaoResponse(ProdutoResponse produtoValidado, ResultadoSimulacao resultadoSimulacao, 
                            LocalDateTime dataSimulacao, Long clienteId) {
        this.produtoValidado = produtoValidado;
        this.resultadoSimulacao = resultadoSimulacao;
        this.dataSimulacao = dataSimulacao;
        this.clienteId = clienteId;
    }

    // Construtor com ID da simulação
    public SimulacaoResponse(ProdutoResponse produtoValidado, ResultadoSimulacao resultadoSimulacao, 
                            LocalDateTime dataSimulacao, Long clienteId, Long simulacaoId) {
        this(produtoValidado, resultadoSimulacao, dataSimulacao, clienteId);
        this.simulacaoId = simulacaoId;
    }

    // Getters e Setters
    public ProdutoResponse getProdutoValidado() {
        return produtoValidado;
    }

    public void setProdutoValidado(ProdutoResponse produtoValidado) {
        this.produtoValidado = produtoValidado;
    }

    public ResultadoSimulacao getResultadoSimulacao() {
        return resultadoSimulacao;
    }

    public void setResultadoSimulacao(ResultadoSimulacao resultadoSimulacao) {
        this.resultadoSimulacao = resultadoSimulacao;
    }

    public LocalDateTime getDataSimulacao() {
        return dataSimulacao;
    }

    public void setDataSimulacao(LocalDateTime dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getSimulacaoId() {
        return simulacaoId;
    }

    public void setSimulacaoId(Long simulacaoId) {
        this.simulacaoId = simulacaoId;
    }
}