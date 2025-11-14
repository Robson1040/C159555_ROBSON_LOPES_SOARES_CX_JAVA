package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.example.model.Indice;
import org.example.model.TipoProduto;
import org.example.model.TipoRentabilidade;
import org.example.validation.ValidPrazo;

import java.math.BigDecimal;

/**
 * DTO para simulação de investimento
 * Todos os campos de filtro são opcionais, exceto clienteId e valor
 */
@ValidPrazo
public class SimulacaoRequest {

    @NotNull(message = "ID do cliente é obrigatório")
    @JsonProperty("clienteId")
    private Long clienteId;

    @NotNull(message = "Valor do investimento é obrigatório")
    @DecimalMin(value = "1.00", message = "Valor mínimo de investimento é R$ 1,00")
    @DecimalMax(value = "999999999.99", message = "Valor máximo de investimento é R$ 999.999.999,99")
    private BigDecimal valor;

    // Prazo - apenas um dos três pode ser informado
    @JsonProperty("prazoMeses")
    @Min(value = 1, message = "Prazo em meses deve ser no mínimo 1")
    @Max(value = 600, message = "Prazo em meses deve ser no máximo 600 (50 anos)")
    private Integer prazoMeses;

    @JsonProperty("prazoDias")
    @Min(value = 1, message = "Prazo em dias deve ser no mínimo 1")
    @Max(value = 18250, message = "Prazo em dias deve ser no máximo 18.250 (50 anos)")
    private Integer prazoDias;

    @JsonProperty("prazoAnos")
    @Min(value = 1, message = "Prazo em anos deve ser no mínimo 1")
    @Max(value = 50, message = "Prazo em anos deve ser no máximo 50")
    private Integer prazoAnos;

    // Filtros opcionais do produto
    @JsonProperty("tipoProduto")
    private TipoProduto tipoProduto;

    @JsonProperty("tipo_rentabilidade")
    private TipoRentabilidade tipoRentabilidade;

    private Indice indice;

    @JsonProperty("liquidez")
    @Min(value = 0, message = "Liquidez deve ser 0 (sem carência) ou um número positivo de dias")
    @Max(value = 3650, message = "Liquidez máxima é de 3.650 dias (10 anos)")
    private Integer liquidez;

    private Boolean fgc;

    // Construtor padrão
    public SimulacaoRequest() {
    }

    // Construtor completo
    public SimulacaoRequest(Long clienteId, BigDecimal valor, Integer prazoMeses, Integer prazoDias, 
                           Integer prazoAnos, TipoProduto tipoProduto, TipoRentabilidade tipoRentabilidade,
                           Indice indice, Integer liquidez, Boolean fgc) {
        this.clienteId = clienteId;
        this.valor = valor;
        this.prazoMeses = prazoMeses;
        this.prazoDias = prazoDias;
        this.prazoAnos = prazoAnos;
        this.tipoProduto = tipoProduto;
        this.tipoRentabilidade = tipoRentabilidade;
        this.indice = indice;
        this.liquidez = liquidez;
        this.fgc = fgc;
    }

    /**
     * Calcula o prazo em dias baseado no campo informado
     * @return prazo em dias
     */
    public int getPrazoEmDias() {
        if (prazoDias != null && prazoDias > 0) {
            return prazoDias;
        }
        if (prazoMeses != null && prazoMeses > 0) {
            return prazoMeses * 30; // Aproximação
        }
        if (prazoAnos != null && prazoAnos > 0) {
            return prazoAnos * 365; // Aproximação
        }
        return 365; // Default 1 ano
    }

    /**
     * Calcula o prazo em meses baseado no campo informado
     * @return prazo em meses
     */
    public int getPrazoEmMeses() {
        if (prazoMeses != null && prazoMeses > 0) {
            return prazoMeses;
        }
        if (prazoDias != null && prazoDias > 0) {
            return Math.max(1, prazoDias / 30); // Pelo menos 1 mês
        }
        if (prazoAnos != null && prazoAnos > 0) {
            return prazoAnos * 12;
        }
        return 12; // Default 1 ano
    }

    // Getters e Setters
    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public TipoRentabilidade getTipoRentabilidade() {
        return tipoRentabilidade;
    }

    public void setTipoRentabilidade(TipoRentabilidade tipoRentabilidade) {
        this.tipoRentabilidade = tipoRentabilidade;
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

    public Boolean getFgc() {
        return fgc;
    }

    public void setFgc(Boolean fgc) {
        this.fgc = fgc;
    }
}