package br.gov.caixa.api.investimentos.model.produto;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.validation.ValidRentabilidadeIndice;

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

    // Construtor padrão
    public Produto()
    {
        this.pontuacao = 0;
    }

    // Construtor com parâmetros
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

    public NivelRisco getRisco()
    {
        if(tipo.equals(TipoProduto.TESOURO_DIRETO))
        {
            return NivelRisco.BAIXO;
        }

        if(tipo.equals(TipoProduto.POUPANCA)){
            return NivelRisco.BAIXO;
        }

        if(tipo.equals(TipoProduto.ACAO) || tipo.equals(TipoProduto.FUNDO)){
            return NivelRisco.ALTO;
        }

        if(tipo.equals(TipoProduto.CDB) && Boolean.TRUE.equals(fgc) && tipoRentabilidade.equals(TipoRentabilidade.POS) && indice.equals(Indice.CDI))
        {
            return NivelRisco.BAIXO;
        }

        if(tipo.equals(TipoProduto.CDB) && Boolean.TRUE.equals(fgc) &&  tipoRentabilidade.equals(TipoRentabilidade.POS) && indice.equals(Indice.IPCA))
        {
            return NivelRisco.MEDIO;
        }

        int pontuacaoRisco = 0;
        
        // 1. ANÁLISE FGC (Fundo Garantidor de Créditos) - Fator mais importante
        if (Boolean.TRUE.equals(fgc)) {
            pontuacaoRisco += 0; // Garantia governamental = sem risco adicional
        } else {
            pontuacaoRisco += 30; // Sem garantia = risco significativo
        }

        // 3. ANÁLISE DE LIQUIDEZ - Capacidade de conversão em dinheiro
        if (liquidez != null) {
            if (liquidez == -1) {
                pontuacaoRisco += 25; 
            } else if (liquidez == 0) {
                pontuacaoRisco += 0;  
            } else if (liquidez <= 30) {
                pontuacaoRisco += 5;  
            } else if (liquidez <= 180) {
                pontuacaoRisco += 15; 
            } else if (liquidez <= 365) {
                pontuacaoRisco += 15; 
            } else {
                pontuacaoRisco += 25; 
            }
        }
        
        //Volatilidade da taxa
        if (tipoRentabilidade == TipoRentabilidade.POS) {
            pontuacaoRisco += 30;
        }

        if (minimoDiasInvestimento != null && minimoDiasInvestimento > 0) {
            if (minimoDiasInvestimento <= 30) {
                pontuacaoRisco += 0;
            } else if (minimoDiasInvestimento <= 180) {
                pontuacaoRisco += 15;
            } else {
                pontuacaoRisco += 25;
            }
        }
        
		System.out.println("RISCO: " + pontuacaoRisco);
		System.out.println("RISCO: " + fgc);
		System.out.println("RISCO: " + Boolean.TRUE.equals(fgc));
		
        // 6. CLASSIFICAÇÃO FINAL baseada na pontuação total
        if (pontuacaoRisco <= 30) {
            return NivelRisco.BAIXO;
        } else if (pontuacaoRisco <= 70) {
            return NivelRisco.MEDIO;
        } else {
            return NivelRisco.ALTO;
        }
    }

    public double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(double pontuacao) {
        this.pontuacao = pontuacao;
    }
}

