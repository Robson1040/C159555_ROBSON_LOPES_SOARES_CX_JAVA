package br.gov.caixa.api.investimentos.model.investimento;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa um investimento efetivado
 */
@Entity
@Table(name = "investimento")
public class Investimento extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @NotNull
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotNull
    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @NotNull
    @DecimalMin(value = "1.00")
    @DecimalMax(value = "999999999.99")
    @Column(name = "valor", precision = 19, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(name = "prazo_meses")
    private Integer prazoMeses;

    @Column(name = "prazo_dias")
    private Integer prazoDias;

    @Column(name = "prazo_anos")
    private Integer prazoAnos;

    @NotNull
    @Column(name = "data", nullable = false)
    private LocalDate data;

    // Campos copiados do Produto para snapshot
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoProduto tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_rentabilidade", nullable = false)
    private TipoRentabilidade tipoRentabilidade;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "rentabilidade", precision = 10, scale = 4, nullable = false)
    private BigDecimal rentabilidade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_rentabilidade", nullable = false)
    private PeriodoRentabilidade periodoRentabilidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "indice")
    private Indice indice;

    @NotNull
    @Min(-1)
    @Column(name = "liquidez", nullable = false)
    private Integer liquidez;

    @NotNull
    @Min(0)
    @Column(name = "minimo_dias_investimento", nullable = false)
    private Integer minimoDiasInvestimento;

    @NotNull
    @Column(name = "fgc", nullable = false)
    private Boolean fgc;

    public Investimento() 
	{
		this.data = LocalDate.now();
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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


