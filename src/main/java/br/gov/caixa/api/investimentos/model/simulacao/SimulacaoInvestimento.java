package br.gov.caixa.api.investimentos.model.simulacao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import br.gov.caixa.api.investimentos.dto.simulacao.ResultadoSimulacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade para persistir simulações de investimento realizadas
 * Armazena o histórico completo de todas as simulações feitas pelos clientes
 */
@Entity
@Table(name = "simulacao_investimento")
public class SimulacaoInvestimento extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    public Long id;

    @NotNull
    @Positive
    @Column(name = "cliente_id", nullable = false)
    public Long clienteId;

    @NotNull
    @Column(name = "produto", nullable = false, length = 255)
    public String produto;

    @NotNull
    @Column(name = "produto_id", nullable = false)
    public Long produtoId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Valor investido deve ser maior que zero")
    @Column(name = "valor_investido", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorInvestido;

    @NotNull
    @DecimalMin(value = "0.00", message = "Valor final não pode ser negativo")
    @Column(name = "valor_final", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorFinal;

    @Column(name = "prazo_meses")
    public Integer prazoMeses;

    @Column(name = "prazo_dias")
    public Integer prazoDias;

    @Column(name = "prazo_anos")
    public Integer prazoAnos;

    @NotNull
    @Column(name = "data_simulacao", nullable = false)
    public LocalDateTime dataSimulacao;

    @Column(name = "rentabilidade_efetiva", precision = 10, scale = 4)
    public BigDecimal rentabilidadeEfetiva;

    @Column(name = "rendimento", precision = 15, scale = 2)
    public BigDecimal rendimento;

    @Column(name = "valor_simulado")
    public Boolean valorSimulado;

    @Column(name = "cenario_simulacao", length = 500)
    public String cenarioSimulacao;

    // Construtor padrão
    public SimulacaoInvestimento() {
        this.dataSimulacao = LocalDateTime.now();
    }

    // Construtor com parâmetros principais
    public SimulacaoInvestimento(Long clienteId, Long produtoId, String produto, BigDecimal valorInvestido,
                               BigDecimal valorFinal, Integer prazoMeses, Integer prazoDias, 
                               Integer prazoAnos) {
        this();
        this.clienteId = clienteId;
        this.produtoId = produtoId;
        this.produto = produto;
        this.valorInvestido = valorInvestido;
        this.valorFinal = valorFinal;
        this.prazoMeses = prazoMeses;
        this.prazoDias = prazoDias;
        this.prazoAnos = prazoAnos;
    }

    // Construtor completo
    public SimulacaoInvestimento(Long clienteId, Long produtoId, String produto, BigDecimal valorInvestido,
                               BigDecimal valorFinal, Integer prazoMeses, Integer prazoDias,
                               Integer prazoAnos, BigDecimal rentabilidadeEfetiva, 
                               BigDecimal rendimento, Boolean valorSimulado, 
                               String cenarioSimulacao) {
        this(clienteId, produtoId, produto, valorInvestido, valorFinal, prazoMeses, prazoDias, prazoAnos);
        this.rentabilidadeEfetiva = rentabilidadeEfetiva;
        this.rendimento = rendimento;
        this.valorSimulado = valorSimulado;
        this.cenarioSimulacao = cenarioSimulacao;
    }



    // Método para facilitar criação a partir de SimulacaoRequest e ResultadoSimulacao
    public static SimulacaoInvestimento fromSimulacao(Long clienteId, Long produtoId, String nomeProduto,
                                                    BigDecimal valorInvestido,
                                                    ResultadoSimulacao resultado) {
        return new SimulacaoInvestimento(
                clienteId,
                produtoId,
                nomeProduto,
                valorInvestido,
                resultado.valorFinal(),
                resultado.prazoMeses(),
                resultado.prazoDias(),
                resultado.prazoAnos(),
                resultado.rentabilidadeEfetiva(),
                resultado.rendimento(),
                resultado.valorSimulado(),
                resultado.cenarioSimulacao()
        );
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
