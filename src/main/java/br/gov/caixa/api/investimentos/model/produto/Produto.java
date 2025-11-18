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

    /**
     * Método derivado que calcula o nível de risco do produto de forma mais precisa,
     * considerando múltiplos fatores além do FGC e tipo de renda
     * 
     * @return NivelRisco calculado baseado em análise abrangente
     */
    /**
     * Calcula o nível de risco do produto baseado em critérios de mercado
     * sem depender do tipo específico do produto, usando apenas:
     * - FGC (garantia governamental)
     * - Rentabilidade (quanto maior, maior o risco)
     * - Liquidez (quanto menor, maior o risco)
     * - Tipo de rentabilidade (pós-fixado tem mais volatilidade)
     * - Mínimo de dias de investimento (carência afeta liquidez)
     */
    public NivelRisco getRisco() {
        // Sistema de pontuação de risco (0-100, onde maior = mais arriscado)
        int pontuacaoRisco = 0;
        
        // 1. ANÁLISE FGC (Fundo Garantidor de Créditos) - Fator mais importante
        if (Boolean.TRUE.equals(fgc)) {
            pontuacaoRisco += 0; // Garantia governamental = sem risco adicional
        } else {
            pontuacaoRisco += 30; // Sem garantia = risco significativo
        }

        if(tipo.equals(TipoProduto.TESOURO_DIRETO))
        {
            pontuacaoRisco += 15;
        }

        // 2. ANÁLISE DE RENTABILIDADE - Princípio básico: maior retorno = maior risco
        if (rentabilidade != null) {
            double rentabilidadeAnual = rentabilidade.doubleValue();
            
            // Ajustar rentabilidade para base anual se necessário
            if (periodoRentabilidade == PeriodoRentabilidade.AO_MES) {
                rentabilidadeAnual = rentabilidadeAnual * 12;
            } else if (periodoRentabilidade == PeriodoRentabilidade.AO_DIA) {
                rentabilidadeAnual = rentabilidadeAnual * 365;
            }
            
            // Escalas baseadas em taxas de mercado brasileiro (Nov 2025)
            if (rentabilidadeAnual <= 6.0) {
                pontuacaoRisco += 0;  // Conservador (≤ SELIC)
            } else if (rentabilidadeAnual <= 12.0) {
                pontuacaoRisco += 15; // Moderado (até 2x SELIC)
            } else if (rentabilidadeAnual <= 20.0) {
                pontuacaoRisco += 30; // Agressivo (até 3x SELIC)
            } else {
                pontuacaoRisco += 45; // Muito arriscado (>3x SELIC)
            }
        }
        
        // 3. ANÁLISE DE LIQUIDEZ - Capacidade de conversão em dinheiro
        if (liquidez != null) {
            if (liquidez == -1) {
                pontuacaoRisco += 25; // Sem liquidez = risco alto
            } else if (liquidez == 0) {
                pontuacaoRisco += 0;  // Liquidez imediata = sem risco
            } else if (liquidez <= 30) {
                pontuacaoRisco += 5;  // Liquidez mensal = risco baixo
            } else if (liquidez <= 180) {
                pontuacaoRisco += 10; // Liquidez semestral = risco moderado
            } else if (liquidez <= 365) {
                pontuacaoRisco += 15; // Liquidez anual = risco médio
            } else {
                pontuacaoRisco += 20; // Liquidez > 1 ano = risco alto
            }
        }
        
        // 4. ANÁLISE TIPO DE RENTABILIDADE - Volatilidade da taxa
        if (tipoRentabilidade == TipoRentabilidade.POS) {
            pontuacaoRisco += 10; // Pós-fixado = sujeito a variações
        }
        // PRE-fixado não adiciona risco (taxa conhecida)
        
        // 5. ANÁLISE DE CARÊNCIA - Impacto na liquidez
        if (minimoDiasInvestimento != null && minimoDiasInvestimento > 0) {
            if (minimoDiasInvestimento <= 30) {
                pontuacaoRisco += 2;  // Carência curta = risco mínimo
            } else if (minimoDiasInvestimento <= 180) {
                pontuacaoRisco += 5;  // Carência média = risco baixo
            } else {
                pontuacaoRisco += 8;  // Carência longa = risco moderado
            }
        }
        
        // 6. CLASSIFICAÇÃO FINAL baseada na pontuação total
        if (pontuacaoRisco <= 20) {
            return NivelRisco.BAIXO;   // 0-20: Produtos conservadores, garantidos
        } else if (pontuacaoRisco <= 50) {
            return NivelRisco.MEDIO;   // 21-50: Produtos moderados, algum risco
        } else {
            return NivelRisco.ALTO;    // 51+: Produtos arriscados, sem garantias
        }
    }

    public double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(double pontuacao) {
        this.pontuacao = pontuacao;
    }
}

