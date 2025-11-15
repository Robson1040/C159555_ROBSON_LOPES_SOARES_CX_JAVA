package br.gov.caixa.api.investimentos.repository.simulacao;

import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface para operações de banco de dados da entidade SimulacaoInvestimento
 */
public interface ISimulacaoInvestimentoRepository extends PanacheRepository<SimulacaoInvestimento> {

    /**
     * Busca todas as simulações de um cliente
     */
    List<SimulacaoInvestimento> findByClienteId(Long clienteId);

    /**
     * Busca simulações de um cliente ordenadas por data (mais recentes primeiro)
     */
    List<SimulacaoInvestimento> findByClienteIdOrderByDate(Long clienteId);

    /**
     * Busca simulações por produto
     */
    List<SimulacaoInvestimento> findByProduto(String produto);

    /**
     * Busca simulações por faixa de valor investido
     */
    List<SimulacaoInvestimento> findByValorInvestidoRange(BigDecimal minValue, BigDecimal maxValue);

    /**
     * Busca simulações por período
     */
    List<SimulacaoInvestimento> findByDataRange(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca simulações que usaram valores simulados (dinâmicos)
     */
    List<SimulacaoInvestimento> findSimulacoesComValoresSimulados();

    /**
     * Conta total de simulações por cliente
     */
    long countByClienteId(Long clienteId);

    /**
     * Busca a última simulação de um cliente
     */
    SimulacaoInvestimento findLastByClienteId(Long clienteId);

    /**
     * Calcula total investido por um cliente em simulações
     */
    BigDecimal getTotalInvestidoByClienteId(Long clienteId);
}