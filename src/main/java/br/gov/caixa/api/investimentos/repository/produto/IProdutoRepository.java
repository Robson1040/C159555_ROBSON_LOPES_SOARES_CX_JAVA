package br.gov.caixa.api.investimentos.repository.produto;

import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;


public interface IProdutoRepository extends PanacheRepository<Produto> {

    
    List<Produto> findByTipo(TipoProduto tipo);

    
    List<Produto> findByTipoRentabilidade(TipoRentabilidade tipoRentabilidade);

    
    List<Produto> findByFgc(Boolean fgc);

    
    List<Produto> findComLiquidezImediata();

    
    List<Produto> findSemLiquidez();

    
    List<Produto> findByNomeContaining(String nome);
}