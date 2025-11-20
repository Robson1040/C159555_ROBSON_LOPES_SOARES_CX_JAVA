package br.gov.caixa.api.investimentos.repository.investimento;

import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

public interface IInvestimentoRepository extends PanacheRepository<Investimento> {

    List<Investimento> findByClienteId(Long clienteId);

    List<Investimento> findByProdutoId(Long produtoId);

    List<Investimento> findByClienteIdOrderByDate(Long clienteId);
}