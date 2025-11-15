package br.gov.caixa.api.investimentos.repository.produto;

import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

/**
 * Interface para operações de banco de dados da entidade Produto
 */
public interface IProdutoRepository extends PanacheRepository<Produto> {

    /**
     * Busca produtos por tipo
     */
    List<Produto> findByTipo(TipoProduto tipo);

    /**
     * Busca produtos por tipo de rentabilidade
     */
    List<Produto> findByTipoRentabilidade(TipoRentabilidade tipoRentabilidade);

    /**
     * Busca produtos por FGC
     */
    List<Produto> findByFgc(Boolean fgc);

    /**
     * Busca produtos com liquidez imediata
     */
    List<Produto> findComLiquidezImediata();

    /**
     * Busca produtos sem liquidez
     */
    List<Produto> findSemLiquidez();

    /**
     * Busca produtos por nome (contendo a string)
     */
    List<Produto> findByNomeContaining(String nome);
}