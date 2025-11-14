package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.model.Indice;
import org.example.model.NivelRisco;
import org.example.model.PeriodoRentabilidade;
import org.example.model.TipoProduto;
import org.example.model.TipoRentabilidade;

import java.math.BigDecimal;

public class ProdutoResponse {

    private Long id;
    private String nome;
    private TipoProduto tipo;

    @JsonProperty("tipo_rentabilidade")
    private TipoRentabilidade tipoRentabilidade;

    private BigDecimal rentabilidade;

    @JsonProperty("periodo_rentabilidade")
    private PeriodoRentabilidade periodoRentabilidade;

    private Indice indice;

    private Integer liquidez;

    @JsonProperty("minimo_dias_investimento")
    private Integer minimoDiasInvestimento;

    private Boolean fgc;

    private NivelRisco risco;

    // Construtor padrão
    public ProdutoResponse() {
    }

    // Construtor com todos os parâmetros
    public ProdutoResponse(Long id, String nome, TipoProduto tipo, TipoRentabilidade tipoRentabilidade,
                          BigDecimal rentabilidade, PeriodoRentabilidade periodoRentabilidade,
                          Indice indice, Integer liquidez, Integer minimoDiasInvestimento, Boolean fgc, NivelRisco risco) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.tipoRentabilidade = tipoRentabilidade;
        this.rentabilidade = rentabilidade;
        this.periodoRentabilidade = periodoRentabilidade;
        this.indice = indice;
        this.liquidez = liquidez;
        this.minimoDiasInvestimento = minimoDiasInvestimento;
        this.fgc = fgc;
        this.risco = risco;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProduto tipo) {
        this.tipo = tipo;
    }

    public TipoRentabilidade getTipoRentabilidade() {
        return tipoRentabilidade;
    }

    public void setTipoRentabilidade(TipoRentabilidade tipoRentabilidade) {
        this.tipoRentabilidade = tipoRentabilidade;
    }

    public BigDecimal getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(BigDecimal rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public PeriodoRentabilidade getPeriodoRentabilidade() {
        return periodoRentabilidade;
    }

    public void setPeriodoRentabilidade(PeriodoRentabilidade periodoRentabilidade) {
        this.periodoRentabilidade = periodoRentabilidade;
    }

    public Indice getIndice() {
        return indice;
    }

    public void setIndice(Indice indice) {
        this.indice = indice;
    }

    public Integer getLiquidez() {
        return liquidez;
    }

    public void setLiquidez(Integer liquidez) {
        this.liquidez = liquidez;
    }

    public Integer getMinimoDiasInvestimento() {
        return minimoDiasInvestimento;
    }

    public void setMinimoDiasInvestimento(Integer minimoDiasInvestimento) {
        this.minimoDiasInvestimento = minimoDiasInvestimento;
    }

    public Boolean getFgc() {
        return fgc;
    }

    public void setFgc(Boolean fgc) {
        this.fgc = fgc;
    }

    public NivelRisco getRisco() {
        return risco;
    }

    public void setRisco(NivelRisco risco) {
        this.risco = risco;
    }
}