package br.gov.caixa.api.investimentos.repository.produto;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;

import java.util.List;

/**
 * Repository para operações de banco de dados da entidade Produto
 */
@ApplicationScoped
public class ProdutoRepository implements IProdutoRepository {

    /**
     * Busca produtos por tipo
     */
    public List<Produto> findByTipo(TipoProduto tipo) {
        return find("tipo", tipo).list();
    }

    /**
     * Busca produtos por tipo de rentabilidade
     */
    public List<Produto> findByTipoRentabilidade(TipoRentabilidade tipoRentabilidade) {
        return find("tipoRentabilidade", tipoRentabilidade).list();
    }

    /**
     * Busca produtos por FGC
     */
    public List<Produto> findByFgc(Boolean fgc) {
        return find("fgc", fgc).list();
    }

    /**
     * Busca produtos com liquidez imediata
     */
    public List<Produto> findComLiquidezImediata() {
        return find("liquidez", 0).list();
    }

    /**
     * Busca produtos sem liquidez
     */
    public List<Produto> findSemLiquidez() {
        return find("liquidez", -1).list();
    }

    /**
     * Busca produtos por nome (contendo a string)
     */
    public List<Produto> findByNomeContaining(String nome) {
        return find("lower(nome) like lower(?1)", "%" + nome + "%").list();
    }
}