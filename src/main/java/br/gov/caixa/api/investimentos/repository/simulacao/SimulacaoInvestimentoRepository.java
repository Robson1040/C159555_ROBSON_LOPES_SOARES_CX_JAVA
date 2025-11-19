package br.gov.caixa.api.investimentos.repository.simulacao;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para operações de banco de dados da entidade SimulacaoInvestimento
 */
@ApplicationScoped
public class SimulacaoInvestimentoRepository implements ISimulacaoInvestimentoRepository {

    /**
     * Busca todas as simulações de um cliente
     */
    public List<SimulacaoInvestimento> findByClienteId(Long clienteId) {
        return find("clienteId", clienteId).list();
    }

    /**
     * Busca simulações de um cliente ordenadas por data (mais recentes primeiro)
     */
    public List<SimulacaoInvestimento> findByClienteIdOrderByDate(Long clienteId) {
        return find("clienteId = ?1 ORDER BY dataSimulacao DESC", clienteId).list();
    }

    /**
     * Busca simulações por produto
     */
    public List<SimulacaoInvestimento> findByProduto(String produto) {
        return find("produto", produto).list();
    }

    /**
     * Busca simulações por faixa de valor investido
     */
    public List<SimulacaoInvestimento> findByValorInvestidoRange(BigDecimal minValue, BigDecimal maxValue) {
        return find("valorInvestido BETWEEN ?1 AND ?2", minValue, maxValue).list();
    }

    /**
     * Busca simulações por período
     */
    public List<SimulacaoInvestimento> findByDataRange(LocalDateTime inicio, LocalDateTime fim) {
        return find("dataSimulacao BETWEEN ?1 AND ?2", inicio, fim).list();
    }

    /**
     * Busca simulações que usaram valores simulados (dinâmicos)
     */
    public List<SimulacaoInvestimento> findSimulacoesComValoresSimulados() {
        return find("valorSimulado = true").list();
    }

    /**
     * Conta total de simulações por cliente
     */
    public long countByClienteId(Long clienteId) {
        return count("clienteId", clienteId);
    }

    /**
     * Busca a última simulação de um cliente
     */
    public SimulacaoInvestimento findLastByClienteId(Long clienteId) {
        return find("clienteId = ?1 ORDER BY dataSimulacao DESC", clienteId).firstResult();
    }

   /**
 * Calcula total investido por um cliente em simulações
 */
	public BigDecimal getTotalInvestidoByClienteId(Long clienteId) 
	{
		Object result = find("SELECT SUM(s.valorInvestido) FROM SimulacaoInvestimento s WHERE s.clienteId = ?1", clienteId)
				.singleResult();

		if (result == null) {
			return BigDecimal.ZERO;
		}

		if (result instanceof BigDecimal) {
			return (BigDecimal) result;
		} else if (result instanceof Number) {
			return BigDecimal.valueOf(((Number) result).doubleValue());
		} else {
			throw new IllegalStateException("Tipo inesperado: " + result.getClass());
		}
	}
}