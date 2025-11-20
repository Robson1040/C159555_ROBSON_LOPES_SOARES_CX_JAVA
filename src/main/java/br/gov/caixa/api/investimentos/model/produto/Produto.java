package br.gov.caixa.api.investimentos.model.produto;

import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.validation.ValidRentabilidadeIndice;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
@ValidRentabilidadeIndice
public class Produto extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Tipo do produto é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoProduto tipo;

    @NotNull(message = "Tipo de rentabilidade é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_rentabilidade", nullable = false)
    private TipoRentabilidade tipoRentabilidade;

    @NotNull(message = "Rentabilidade é obrigatória")
    @DecimalMin(value = "0.0", message = "Rentabilidade deve ser maior ou igual a zero")
    @Column(precision = 10, scale = 4, nullable = false)
    private BigDecimal rentabilidade;

    @NotNull(message = "Período de rentabilidade é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_rentabilidade", nullable = false)
    private PeriodoRentabilidade periodoRentabilidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "indice", nullable = true)
    private Indice indice;

    @NotNull(message = "Liquidez é obrigatória")
    @Min(value = -1, message = "Liquidez deve ser -1 (sem liquidez) ou >= 0")
    @Column(name = "liquidez", nullable = false)
    private Integer liquidez;

    @NotNull(message = "Mínimo de dias de investimento é obrigatório")
    @Min(value = 0, message = "Mínimo de dias de investimento deve ser >= 0")
    @Column(name = "minimo_dias_investimento", nullable = false)
    private Integer minimoDiasInvestimento;

    @NotNull(message = "FGC é obrigatório")
    @Column(name = "fgc", nullable = false)
    private Boolean fgc;

    @Transient
    private double pontuacao;

    public Produto() {
        this.pontuacao = 0;
    }

    public Produto(String nome, TipoProduto tipo, TipoRentabilidade tipoRentabilidade,
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

        this.pontuacao = 0;
    }

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

        if (tipo == TipoProduto.TESOURO_DIRETO
                || tipo == TipoProduto.POUPANCA) {
            return NivelRisco.BAIXO;
        }

        if (tipo == TipoProduto.ACAO
                || tipo == TipoProduto.FUNDO || tipo == TipoProduto.DEBENTURE || tipo == TipoProduto.CRI) {
            return NivelRisco.ALTO;
        }

        if (tipo == TipoProduto.LCI
                || tipo == TipoProduto.LCA) {
            return NivelRisco.BAIXO;
        }

        if (tipo == TipoProduto.CDB && Boolean.TRUE.equals(fgc)) {
            if (tipoRentabilidade == TipoRentabilidade.POS && indice == Indice.CDI) {
                return NivelRisco.BAIXO;
            }
            if (tipoRentabilidade == TipoRentabilidade.POS && indice == Indice.IPCA) {
                return NivelRisco.MEDIO;
            }
        }

        int p = 0;

        if (Boolean.TRUE.equals(fgc)) {
            p += 0;
        } else {
            p += 20;
        }

        if (tipo == TipoProduto.CDB) {
            if (tipoRentabilidade == TipoRentabilidade.POS) {
                switch (indice) {
                    case CDI -> p += 0;
                    case IPCA -> p += 5;
                    case IGP_M -> p += 10;
                }
            }
        } else {
            p += 5;
        }

        if (liquidez != null) {
            if (liquidez <= 0) p += 0;
            else if (liquidez <= 30) p += 2;
            else if (liquidez <= 180) p += 6;
            else p += 10;
        }

        if (minimoDiasInvestimento != null && minimoDiasInvestimento > 0) {
            if (minimoDiasInvestimento <= 30) p += 0;
            else if (minimoDiasInvestimento <= 180) p += 5;
            else p += 10;
        }

        if (p <= 15) return NivelRisco.BAIXO;
        if (p <= 35) return NivelRisco.MEDIO;
        return NivelRisco.ALTO;
    }

    public double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(double pontuacao) {
        this.pontuacao = pontuacao;
    }
}

