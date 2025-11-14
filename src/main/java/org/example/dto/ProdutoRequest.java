package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.example.model.Indice;
import org.example.model.PeriodoRentabilidade;
import org.example.model.TipoProduto;
import org.example.model.TipoRentabilidade;
import org.example.validation.ValidRentabilidadeIndice;

import java.math.BigDecimal;

@ValidRentabilidadeIndice
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    private String nome;

    @NotNull(message = "Tipo do produto é obrigatório")
    private TipoProduto tipo;

    @NotNull(message = "Tipo de rentabilidade é obrigatório")
    @JsonProperty("tipo_rentabilidade")
    private TipoRentabilidade tipoRentabilidade;

    @NotNull(message = "Rentabilidade é obrigatória")
    @DecimalMin(value = "0.0", message = "Rentabilidade deve ser maior ou igual a zero")
    private BigDecimal rentabilidade;

    @NotNull(message = "Período de rentabilidade é obrigatório")
    @JsonProperty("periodo_rentabilidade")
    private PeriodoRentabilidade periodoRentabilidade;

    private Indice indice;

    @NotNull(message = "Liquidez é obrigatória")
    @Min(value = -1, message = "Liquidez deve ser -1 (sem liquidez) ou >= 0")
    private Integer liquidez;

    @NotNull(message = "Mínimo de dias de investimento é obrigatório")
    @Min(value = 0, message = "Mínimo de dias de investimento deve ser >= 0")
    @JsonProperty("minimo_dias_investimento")
    private Integer minimoDiasInvestimento;

    @NotNull(message = "FGC é obrigatório")
    private Boolean fgc;

    // Construtor padrão
    public ProdutoRequest() {
    }

    // Construtor com todos os parâmetros
    public ProdutoRequest(String nome, TipoProduto tipo, TipoRentabilidade tipoRentabilidade,
                         BigDecimal rentabilidade, PeriodoRentabilidade periodoRentabilidade,
                         Indice indice, Integer liquidez, Integer minimoDiasInvestimento, Boolean fgc) {
        this.nome = nome;
        this.tipo = tipo;
        this.tipoRentabilidade = tipoRentabilidade;
        this.rentabilidade = rentabilidade;
        this.periodoRentabilidade = periodoRentabilidade;
        this.indice = indice;
        this.liquidez = liquidez;
        this.minimoDiasInvestimento = minimoDiasInvestimento;
        this.fgc = fgc;
    }

    // Getters e Setters
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
}