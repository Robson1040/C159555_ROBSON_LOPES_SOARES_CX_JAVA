package br.gov.caixa.api.investimentos.repository.produto;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;

import java.util.List;


@ApplicationScoped
public class ProdutoRepository implements IProdutoRepository {

    
    public List<Produto> findByTipo(TipoProduto tipo) {
        return find("tipo", tipo).list();
    }

    
    public List<Produto> findByTipoRentabilidade(TipoRentabilidade tipoRentabilidade) {
        return find("tipoRentabilidade", tipoRentabilidade).list();
    }

    
    public List<Produto> findByFgc(Boolean fgc) {
        return find("fgc", fgc).list();
    }

    
    public List<Produto> findComLiquidezImediata() {
        return find("liquidez", 0).list();
    }

    
    public List<Produto> findSemLiquidez() {
        return find("liquidez", -1).list();
    }

    
    public List<Produto> findByNomeContaining(String nome) {
        return find("lower(nome) like lower(?1)", "%" + nome + "%").list();
    }
}