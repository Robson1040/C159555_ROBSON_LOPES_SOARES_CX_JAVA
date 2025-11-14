package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * DTO para o resultado da simulação financeira
 */
public class ResultadoSimulacao {

    @JsonProperty("valorFinal")
    private BigDecimal valorFinal;

    @JsonProperty("rentabilidadeEfetiva")
    private BigDecimal rentabilidadeEfetiva;

    @JsonProperty("prazoMeses")
    private Integer prazoMeses;

    @JsonProperty("prazoDias")
    private Integer prazoDias;

    @JsonProperty("prazoAnos")
    private Integer prazoAnos;

    @JsonProperty("valorInvestido")
    private BigDecimal valorInvestido;

    @JsonProperty("rendimento")
    private BigDecimal rendimento;

    @JsonProperty("valorSimulado")
    private Boolean valorSimulado;

    @JsonProperty("cenarioSimulacao")
    private String cenarioSimulacao;

    // Construtor padrão
    public ResultadoSimulacao() {
    }

    // Construtor completo
    public ResultadoSimulacao(BigDecimal valorFinal, BigDecimal rentabilidadeEfetiva, 
                             Integer prazoMeses, Integer prazoDias, Integer prazoAnos,
                             BigDecimal valorInvestido, BigDecimal rendimento,
                             Boolean valorSimulado, String cenarioSimulacao) {
        this.valorFinal = valorFinal;
        this.rentabilidadeEfetiva = rentabilidadeEfetiva;
        this.prazoMeses = prazoMeses;
        this.prazoDias = prazoDias;
        this.prazoAnos = prazoAnos;
        this.valorInvestido = valorInvestido;
        this.rendimento = rendimento;
        this.valorSimulado = valorSimulado;
        this.cenarioSimulacao = cenarioSimulacao;
    }

    // Getters e Setters
    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public BigDecimal getRentabilidadeEfetiva() {
        return rentabilidadeEfetiva;
    }

    public void setRentabilidadeEfetiva(BigDecimal rentabilidadeEfetiva) {
        this.rentabilidadeEfetiva = rentabilidadeEfetiva;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public void setPrazoMeses(Integer prazoMeses) {
        this.prazoMeses = prazoMeses;
    }

    public Integer getPrazoDias() {
        return prazoDias;
    }

    public void setPrazoDias(Integer prazoDias) {
        this.prazoDias = prazoDias;
    }

    public Integer getPrazoAnos() {
        return prazoAnos;
    }

    public void setPrazoAnos(Integer prazoAnos) {
        this.prazoAnos = prazoAnos;
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public BigDecimal getRendimento() {
        return rendimento;
    }

    public void setRendimento(BigDecimal rendimento) {
        this.rendimento = rendimento;
    }

    public Boolean getValorSimulado() {
        return valorSimulado;
    }

    public void setValorSimulado(Boolean valorSimulado) {
        this.valorSimulado = valorSimulado;
    }

    public String getCenarioSimulacao() {
        return cenarioSimulacao;
    }

    public void setCenarioSimulacao(String cenarioSimulacao) {
        this.cenarioSimulacao = cenarioSimulacao;
    }
}