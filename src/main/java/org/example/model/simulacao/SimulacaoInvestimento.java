package org.example.model.simulacao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.dto.simulacao.ResultadoSimulacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    public SimulacaoInvestimento(Long clienteId, String produto, BigDecimal valorInvestido, 
                               BigDecimal valorFinal, Integer prazoMeses, Integer prazoDias, 
                               Integer prazoAnos) {
        this();
        this.clienteId = clienteId;
        this.produto = produto;
        this.valorInvestido = valorInvestido;
        this.valorFinal = valorFinal;
        this.prazoMeses = prazoMeses;
        this.prazoDias = prazoDias;
        this.prazoAnos = prazoAnos;
    }

    // Construtor completo
    public SimulacaoInvestimento(Long clienteId, String produto, BigDecimal valorInvestido,
                               BigDecimal valorFinal, Integer prazoMeses, Integer prazoDias,
                               Integer prazoAnos, BigDecimal rentabilidadeEfetiva, 
                               BigDecimal rendimento, Boolean valorSimulado, 
                               String cenarioSimulacao) {
        this(clienteId, produto, valorInvestido, valorFinal, prazoMeses, prazoDias, prazoAnos);
        this.rentabilidadeEfetiva = rentabilidadeEfetiva;
        this.rendimento = rendimento;
        this.valorSimulado = valorSimulado;
        this.cenarioSimulacao = cenarioSimulacao;
    }

    // Métodos de busca customizados
    
    /**
     * Busca todas as simulações de um cliente
     */
    public static List<SimulacaoInvestimento> findByClienteId(Long clienteId) {
        return find("clienteId", clienteId).list();
    }

    /**
     * Busca simulações de um cliente ordenadas por data (mais recentes primeiro)
     */
    public static List<SimulacaoInvestimento> findByClienteIdOrderByDate(Long clienteId) {
        return find("clienteId = ?1 ORDER BY dataSimulacao DESC", clienteId).list();
    }

    /**
     * Busca simulações por produto
     */
    public static List<SimulacaoInvestimento> findByProduto(String produto) {
        return find("produto", produto).list();
    }

    /**
     * Busca simulações por faixa de valor investido
     */
    public static List<SimulacaoInvestimento> findByValorInvestidoRange(BigDecimal minValue, BigDecimal maxValue) {
        return find("valorInvestido BETWEEN ?1 AND ?2", minValue, maxValue).list();
    }

    /**
     * Busca simulações por período
     */
    public static List<SimulacaoInvestimento> findByDataRange(LocalDateTime inicio, LocalDateTime fim) {
        return find("dataSimulacao BETWEEN ?1 AND ?2", inicio, fim).list();
    }

    /**
     * Busca simulações que usaram valores simulados (dinâmicos)
     */
    public static List<SimulacaoInvestimento> findSimulacoesComValoresSimulados() {
        return find("valorSimulado = true").list();
    }

    /**
     * Conta total de simulações por cliente
     */
    public static long countByClienteId(Long clienteId) {
        return count("clienteId", clienteId);
    }

    /**
     * Busca a última simulação de um cliente
     */
    public static SimulacaoInvestimento findLastByClienteId(Long clienteId) {
        return find("clienteId = ?1 ORDER BY dataSimulacao DESC", clienteId).firstResult();
    }

    /**
     * Calcula total investido por um cliente em simulações
     */
    public static BigDecimal getTotalInvestidoByClienteId(Long clienteId) {
        Object result = find("SELECT SUM(s.valorInvestido) FROM SimulacaoInvestimento s WHERE s.clienteId = ?1", clienteId)
                .singleResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    // Método para facilitar criação a partir de SimulacaoRequest e ResultadoSimulacao
    public static SimulacaoInvestimento fromSimulacao(Long clienteId, String nomeProduto, 
                                                    BigDecimal valorInvestido,
                                                    ResultadoSimulacao resultado) {
        return new SimulacaoInvestimento(
                clienteId,
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
}
