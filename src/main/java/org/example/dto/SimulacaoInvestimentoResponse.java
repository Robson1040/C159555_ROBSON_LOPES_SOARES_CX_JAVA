package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.model.SimulacaoInvestimento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de simulações persistidas
 * Usado nos endpoints de consulta de histórico
 */
public class SimulacaoInvestimentoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("clienteId")
    private Long clienteId;

    @JsonProperty("produto")
    private String produto;

    @JsonProperty("valorInvestido")
    private BigDecimal valorInvestido;

    @JsonProperty("valorFinal")
    private BigDecimal valorFinal;

    @JsonProperty("prazoMeses")
    private Integer prazoMeses;

    @JsonProperty("prazoDias")
    private Integer prazoDias;

    @JsonProperty("prazoAnos")
    private Integer prazoAnos;

    @JsonProperty("dataSimulacao")
    private LocalDateTime dataSimulacao;

    @JsonProperty("rentabilidadeEfetiva")
    private BigDecimal rentabilidadeEfetiva;

    @JsonProperty("rendimento")
    private BigDecimal rendimento;

    @JsonProperty("valorSimulado")
    private Boolean valorSimulado;

    @JsonProperty("cenarioSimulacao")
    private String cenarioSimulacao;

    // Construtores
    public SimulacaoInvestimentoResponse() {}

    public SimulacaoInvestimentoResponse(SimulacaoInvestimento entidade) {
        this.id = entidade.id;
        this.clienteId = entidade.clienteId;
        this.produto = entidade.produto;
        this.valorInvestido = entidade.valorInvestido;
        this.valorFinal = entidade.valorFinal;
        this.prazoMeses = entidade.prazoMeses;
        this.prazoDias = entidade.prazoDias;
        this.prazoAnos = entidade.prazoAnos;
        this.dataSimulacao = entidade.dataSimulacao;
        this.rentabilidadeEfetiva = entidade.rentabilidadeEfetiva;
        this.rendimento = entidade.rendimento;
        this.valorSimulado = entidade.valorSimulado;
        this.cenarioSimulacao = entidade.cenarioSimulacao;
    }

    // Método utilitário para converter lista
    public static java.util.List<SimulacaoInvestimentoResponse> fromList(
            java.util.List<SimulacaoInvestimento> entidades) {
        return entidades.stream()
                .map(SimulacaoInvestimentoResponse::new)
                .collect(java.util.stream.Collectors.toList());
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public BigDecimal getValorInvestido() { return valorInvestido; }
    public void setValorInvestido(BigDecimal valorInvestido) { this.valorInvestido = valorInvestido; }

    public BigDecimal getValorFinal() { return valorFinal; }
    public void setValorFinal(BigDecimal valorFinal) { this.valorFinal = valorFinal; }

    public Integer getPrazoMeses() { return prazoMeses; }
    public void setPrazoMeses(Integer prazoMeses) { this.prazoMeses = prazoMeses; }

    public Integer getPrazoDias() { return prazoDias; }
    public void setPrazoDias(Integer prazoDias) { this.prazoDias = prazoDias; }

    public Integer getPrazoAnos() { return prazoAnos; }
    public void setPrazoAnos(Integer prazoAnos) { this.prazoAnos = prazoAnos; }

    public LocalDateTime getDataSimulacao() { return dataSimulacao; }
    public void setDataSimulacao(LocalDateTime dataSimulacao) { this.dataSimulacao = dataSimulacao; }

    public BigDecimal getRentabilidadeEfetiva() { return rentabilidadeEfetiva; }
    public void setRentabilidadeEfetiva(BigDecimal rentabilidadeEfetiva) { this.rentabilidadeEfetiva = rentabilidadeEfetiva; }

    public BigDecimal getRendimento() { return rendimento; }
    public void setRendimento(BigDecimal rendimento) { this.rendimento = rendimento; }

    public Boolean getValorSimulado() { return valorSimulado; }
    public void setValorSimulado(Boolean valorSimulado) { this.valorSimulado = valorSimulado; }

    public String getCenarioSimulacao() { return cenarioSimulacao; }
    public void setCenarioSimulacao(String cenarioSimulacao) { this.cenarioSimulacao = cenarioSimulacao; }
}