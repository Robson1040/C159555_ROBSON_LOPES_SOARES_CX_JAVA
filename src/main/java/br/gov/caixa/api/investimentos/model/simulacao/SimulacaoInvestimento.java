package br.gov.caixa.api.investimentos.model.simulacao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulacao_investimento")
public class SimulacaoInvestimento extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @NotNull
    @Positive
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotNull
    @Column(name = "produto", nullable = false, length = 255)
    private String produto;

    @NotNull
    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Valor investido deve ser maior que zero")
    @Column(name = "valor_investido", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorInvestido;

    @NotNull
    @DecimalMin(value = "0.00", message = "Valor final não pode ser negativo")
    @Column(name = "valor_final", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "prazo_meses")
    private Integer prazoMeses;

    @Column(name = "prazo_dias")
    private Integer prazoDias;

    @Column(name = "prazo_anos")
    private Integer prazoAnos;

    @NotNull
    @Column(name = "data_simulacao", nullable = false)
    private LocalDateTime dataSimulacao;

    @Column(name = "rentabilidade_efetiva", precision = 10, scale = 4)
    private BigDecimal rentabilidadeEfetiva;

    @Column(name = "rendimento", precision = 15, scale = 2)
    private BigDecimal rendimento;

    @Column(name = "valor_simulado")
    private Boolean valorSimulado;

    @Column(name = "cenario_simulacao", length = 500)
    private String cenarioSimulacao;

    public SimulacaoInvestimento() {
        this.dataSimulacao = LocalDateTime.now();
    }

    public SimulacaoInvestimento(Long clienteId, Long produtoId, String produto, BigDecimal valorInvestido,
                                 BigDecimal valorFinal, Integer prazoMeses, Integer prazoDias,
                                 Integer prazoAnos) {
        this();
        this.setClienteId(clienteId);
        this.setProdutoId(produtoId);
        this.setProduto(produto);
        this.setValorInvestido(valorInvestido);
        this.setValorFinal(valorFinal);
        this.setPrazoMeses(prazoMeses);
        this.setPrazoDias(prazoDias);
        this.setPrazoAnos(prazoAnos);
    }

    public SimulacaoInvestimento(Long clienteId, Long produtoId, String produto, BigDecimal valorInvestido,
                                 BigDecimal valorFinal, Integer prazoMeses, Integer prazoDias,
                                 Integer prazoAnos, BigDecimal rentabilidadeEfetiva,
                                 BigDecimal rendimento, Boolean valorSimulado,
                                 String cenarioSimulacao) {
        this(clienteId, produtoId, produto, valorInvestido, valorFinal, prazoMeses, prazoDias, prazoAnos);
        this.setRentabilidadeEfetiva(rentabilidadeEfetiva);
        this.setRendimento(rendimento);
        this.setValorSimulado(valorSimulado);
        this.setCenarioSimulacao(cenarioSimulacao);
    }

    @Override
    public String toString() {
        return "SimulacaoInvestimento{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", produto='" + produto + '\'' +
                ", valorInvestido=" + valorInvestido +
                ", valorFinal=" + valorFinal +
                ", prazoMeses=" + prazoMeses +
                ", prazoDias=" + prazoDias +
                ", prazoAnos=" + prazoAnos +
                ", dataSimulacao=" + dataSimulacao +
                ", valorSimulado=" + valorSimulado +
                '}';
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
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

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
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

    public LocalDateTime getDataSimulacao() {
        return dataSimulacao;
    }

    public void setDataSimulacao(LocalDateTime dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }

    public BigDecimal getRentabilidadeEfetiva() {
        return rentabilidadeEfetiva;
    }

    public void setRentabilidadeEfetiva(BigDecimal rentabilidadeEfetiva) {
        this.rentabilidadeEfetiva = rentabilidadeEfetiva;
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
