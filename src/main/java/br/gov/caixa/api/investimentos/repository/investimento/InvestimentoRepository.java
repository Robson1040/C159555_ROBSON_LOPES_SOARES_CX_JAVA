package br.gov.caixa.api.investimentos.repository.investimento;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;

import java.util.List;


@ApplicationScoped
public class InvestimentoRepository implements IInvestimentoRepository {

    
    public List<Investimento> findByClienteId(Long clienteId) {
        return find("clienteId", clienteId).list();
    }

    
    public List<Investimento> findByProdutoId(Long produtoId) {
        return find("produtoId", produtoId).list();
    }

    
    public List<Investimento> findByClienteIdOrderByDate(Long clienteId) {
        return find("clienteId = ?1 ORDER BY data DESC", clienteId).list();
    }
}