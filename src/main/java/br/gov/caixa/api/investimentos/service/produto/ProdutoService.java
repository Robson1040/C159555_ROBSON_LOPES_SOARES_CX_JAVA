package br.gov.caixa.api.investimentos.service.produto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.exception.produto.ProdutoNotFoundException;
import br.gov.caixa.api.investimentos.mapper.ProdutoMapper;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.repository.produto.IProdutoRepository;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoMapper produtoMapper;

    @Inject
    IProdutoRepository produtoRepository;

    
    public List<ProdutoResponse> listarTodos() {
        List<Produto> produtos = produtoRepository.listAll();
        return produtoMapper.toResponseList(produtos);
    }

    
    public Optional<ProdutoResponse> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Produto.<Produto>findByIdOptional(id)
                .map(produtoMapper::toResponse);
    }

    
    public List<ProdutoResponse> buscarPorTipo(TipoProduto tipo) {
        if (tipo == null) {
            return List.of();
        }

        List<Produto> produtos = produtoRepository.findByTipo(tipo);
        return produtoMapper.toResponseList(produtos);
    }

    
    public List<ProdutoResponse> buscarPorTipoRentabilidade(TipoRentabilidade tipoRentabilidade) {
        if (tipoRentabilidade == null) {
            return List.of();
        }

        List<Produto> produtos = produtoRepository.findByTipoRentabilidade(tipoRentabilidade);
        return produtoMapper.toResponseList(produtos);
    }

    
    public List<ProdutoResponse> buscarProdutosComFgc() {
        List<Produto> produtos = produtoRepository.findByFgc(true);
        return produtoMapper.toResponseList(produtos);
    }

    
    public List<ProdutoResponse> buscarProdutosComLiquidezImediata() {
        List<Produto> produtos = produtoRepository.findComLiquidezImediata();
        return produtoMapper.toResponseList(produtos);
    }

    
    public List<ProdutoResponse> buscarProdutosSemLiquidez() {
        List<Produto> produtos = produtoRepository.findSemLiquidez();
        return produtoMapper.toResponseList(produtos);
    }

    
    public List<ProdutoResponse> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }

        List<Produto> produtos = produtoRepository.findByNomeContaining(nome.trim());
        return produtoMapper.toResponseList(produtos);
    }

    
    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do produto não podem ser nulos");
        }

        validarDadosProduto(request);

        Produto produto = produtoMapper.toEntity(request);

        

        produtoRepository.persist(produto);

        

        return produtoMapper.toResponse(produto);
    }

    
    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        if (request == null) {
            throw new IllegalArgumentException("Dados do produto não podem ser nulos");
        }

        validarDadosProduto(request);

        Produto produto = produtoRepository.findById(id);
        if (produto == null) {
            throw new ProdutoNotFoundException("Produto não encontrado com ID: " + id);
        }

        produtoMapper.updateEntityFromRequest(produto, request);
        produtoRepository.persist(produto);
        
        return produtoMapper.toResponse(produto);
    }

    
    @Transactional
    public void remover(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }

        boolean removido = produtoRepository.deleteById(id);
        if (!removido) {
            throw new ProdutoNotFoundException("Produto não encontrado com ID: " + id);
        }
    }

    
    public boolean existePorId(Long id) {
        if (id == null) {
            return false;
        }
        return produtoRepository.findByIdOptional(id).isPresent();
    }

    
    public long contarTodos() {
        return produtoRepository.count();
    }

    
    @Transactional
    public void limparTodos() {
        produtoRepository.deleteAll();
    }

    
    private void validarDadosProduto(ProdutoRequest request) {
        
        if (request.tipoRentabilidade() == TipoRentabilidade.POS && 
            (request.indice() == null || request.indice().name().equals("NENHUM"))) {
            throw new IllegalArgumentException("Produtos pós-fixados devem ter um índice válido");
        }

        
        if (request.tipoRentabilidade() == TipoRentabilidade.PRE && 
            request.indice() != null && !request.indice().name().equals("NENHUM")) {
            throw new IllegalArgumentException("Produtos pré-fixados não devem ter índice");
        }

        
        if (request.liquidez() < -1) {
            throw new IllegalArgumentException("Liquidez deve ser -1 (sem liquidez) ou >= 0");
        }
    }
}

