package org.example.service.produto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.produto.ProdutoRequest;
import org.example.dto.produto.ProdutoResponse;
import org.example.exception.ProdutoNotFoundException;
import org.example.mapper.ProdutoMapper;
import org.example.model.produto.Produto;
import org.example.enums.produto.TipoProduto;
import org.example.enums.produto.TipoRentabilidade;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoMapper produtoMapper;

    /**
     * Lista todos os produtos
     */
    public List<ProdutoResponse> listarTodos() {
        List<Produto> produtos = Produto.listAll();
        return produtoMapper.toResponseList(produtos);
    }

    /**
     * Busca produto por ID
     */
    public Optional<ProdutoResponse> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Produto.<Produto>findByIdOptional(id)
                .map(produtoMapper::toResponse);
    }

    /**
     * Busca produtos por tipo
     */
    public List<ProdutoResponse> buscarPorTipo(TipoProduto tipo) {
        if (tipo == null) {
            return List.of();
        }

        List<Produto> produtos = Produto.findByTipo(tipo);
        return produtoMapper.toResponseList(produtos);
    }

    /**
     * Busca produtos por tipo de rentabilidade
     */
    public List<ProdutoResponse> buscarPorTipoRentabilidade(TipoRentabilidade tipoRentabilidade) {
        if (tipoRentabilidade == null) {
            return List.of();
        }

        List<Produto> produtos = Produto.findByTipoRentabilidade(tipoRentabilidade);
        return produtoMapper.toResponseList(produtos);
    }

    /**
     * Busca produtos protegidos pelo FGC
     */
    public List<ProdutoResponse> buscarProdutosComFgc() {
        List<Produto> produtos = Produto.findByFgc(true);
        return produtoMapper.toResponseList(produtos);
    }

    /**
     * Busca produtos com liquidez imediata (0 dias)
     */
    public List<ProdutoResponse> buscarProdutosComLiquidezImediata() {
        List<Produto> produtos = Produto.findComLiquidezImediata();
        return produtoMapper.toResponseList(produtos);
    }

    /**
     * Busca produtos sem liquidez
     */
    public List<ProdutoResponse> buscarProdutosSemLiquidez() {
        List<Produto> produtos = Produto.findSemLiquidez();
        return produtoMapper.toResponseList(produtos);
    }

    /**
     * Busca produtos por nome (busca parcial)
     */
    public List<ProdutoResponse> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }

        List<Produto> produtos = Produto.findByNomeContaining(nome.trim());
        return produtoMapper.toResponseList(produtos);
    }

    /**
     * Cria um novo produto
     */
    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do produto não podem ser nulos");
        }

        validarDadosProduto(request);

        Produto produto = produtoMapper.toEntity(request);
        produto.persist();
        
        return produtoMapper.toResponse(produto);
    }

    /**
     * Atualiza um produto existente
     */
    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        if (request == null) {
            throw new IllegalArgumentException("Dados do produto não podem ser nulos");
        }

        validarDadosProduto(request);

        Produto produto = Produto.findById(id);
        if (produto == null) {
            throw new ProdutoNotFoundException("Produto não encontrado com ID: " + id);
        }

        produtoMapper.updateEntityFromRequest(produto, request);
        produto.persist();
        
        return produtoMapper.toResponse(produto);
    }

    /**
     * Remove um produto
     */
    @Transactional
    public void remover(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }

        boolean removido = Produto.deleteById(id);
        if (!removido) {
            throw new ProdutoNotFoundException("Produto não encontrado com ID: " + id);
        }
    }

    /**
     * Verifica se existe produto com ID
     */
    public boolean existePorId(Long id) {
        if (id == null) {
            return false;
        }
        return Produto.findByIdOptional(id).isPresent();
    }

    /**
     * Conta total de produtos
     */
    public long contarTodos() {
        return Produto.count();
    }

    /**
     * Remove todos os produtos (para testes)
     */
    @Transactional
    public void limparTodos() {
        Produto.deleteAll();
    }

    /**
     * Validações de negócio específicas
     */
    private void validarDadosProduto(ProdutoRequest request) {
        // Validação: Se for pós-fixado, deve ter um índice válido (diferente de NENHUM)
        if (request.tipoRentabilidade() == TipoRentabilidade.POS && 
            request.indice().name().equals("NENHUM")) {
            throw new IllegalArgumentException("Produtos pós-fixados devem ter um índice válido");
        }

        // Validação: Se for pré-fixado, o índice deve ser NENHUM
        if (request.tipoRentabilidade() == TipoRentabilidade.PRE && 
            !request.indice().name().equals("NENHUM")) {
            throw new IllegalArgumentException("Produtos pré-fixados não devem ter índice");
        }

        // Validação: Liquidez deve ser -1 ou >= 0
        if (request.liquidez() < -1) {
            throw new IllegalArgumentException("Liquidez deve ser -1 (sem liquidez) ou >= 0");
        }
    }
}

