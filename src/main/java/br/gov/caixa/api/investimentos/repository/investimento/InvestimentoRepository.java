package br.gov.caixa.api.investimentos.repository.investimento;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;

import java.util.List;

/**
 * Repository para operações de banco de dados da entidade Investimento
 */
@ApplicationScoped
public class InvestimentoRepository implements IInvestimentoRepository {

    /**
     * Busca investimentos por cliente
     */
    public List<Investimento> findByClienteId(Long clienteId) {
        return find("clienteId", clienteId).list();
    }

    /**
     * Busca investimentos por produto
     */
    public List<Investimento> findByProdutoId(Long produtoId) {
        return find("produtoId", produtoId).list();
    }

    /**
     * Busca investimentos por cliente ordenados por data
     */
    public List<Investimento> findByClienteIdOrderByDate(Long clienteId) {
        return find("clienteId = ?1 ORDER BY data DESC", clienteId).list();
    }
}