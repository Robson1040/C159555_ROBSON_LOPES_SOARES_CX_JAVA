package org.example.repository.investimento;

import org.example.model.investimento.Investimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

/**
 * Interface para operações de banco de dados da entidade Investimento
 */
public interface IInvestimentoRepository extends PanacheRepository<Investimento> {

    /**
     * Busca investimentos por cliente
     */
    List<Investimento> findByClienteId(Long clienteId);

    /**
     * Busca investimentos por produto
     */
    List<Investimento> findByProdutoId(Long produtoId);

    /**
     * Busca investimentos por cliente ordenados por data
     */
    List<Investimento> findByClienteIdOrderByDate(Long clienteId);
}