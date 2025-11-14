package org.example.repository.produto;

import org.example.model.produto.Produto;
import org.example.enums.produto.TipoProduto;
import org.example.enums.produto.TipoRentabilidade;
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