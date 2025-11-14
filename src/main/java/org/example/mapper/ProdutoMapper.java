package org.example.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.dto.ProdutoRequest;
import org.example.dto.ProdutoResponse;
import org.example.model.Produto;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoMapper {

    /**
     * Converte uma entidade Produto para ProdutoResponse
     */
    public ProdutoResponse toResponse(Produto produto) {
        if (produto == null) {
            return null;
        }

        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getTipo(),
                produto.getTipoRentabilidade(),
                produto.getRentabilidade(),
                produto.getPeriodoRentabilidade(),
                produto.getIndice(),
                produto.getLiquidez(),
                produto.getMinimoDiasInvestimento(),
                produto.getFgc(),
                produto.getRisco()
        );
    }

    /**
     * Converte uma lista de entidades Produto para uma lista de ProdutoResponse
     */
    public List<ProdutoResponse> toResponseList(List<Produto> produtos) {
        if (produtos == null) {
            return null;
        }

        return produtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converte um ProdutoRequest para entidade Produto
     */
    public Produto toEntity(ProdutoRequest request) {
        if (request == null) {
            return null;
        }

        return new Produto(
                request.getNome(),
                request.getTipo(),
                request.getTipoRentabilidade(),
                request.getRentabilidade(),
                request.getPeriodoRentabilidade(),
                request.getIndice(),
                request.getLiquidez(),
                request.getMinimoDiasInvestimento(),
                request.getFgc()
        );
    }

    /**
     * Atualiza uma entidade Produto existente com dados de um ProdutoRequest
     */
    public void updateEntityFromRequest(Produto produto, ProdutoRequest request) {
        if (produto == null || request == null) {
            return;
        }

        produto.setNome(request.getNome());
        produto.setTipo(request.getTipo());
        produto.setTipoRentabilidade(request.getTipoRentabilidade());
        produto.setRentabilidade(request.getRentabilidade());
        produto.setPeriodoRentabilidade(request.getPeriodoRentabilidade());
        produto.setIndice(request.getIndice());
        produto.setLiquidez(request.getLiquidez());
        produto.setMinimoDiasInvestimento(request.getMinimoDiasInvestimento());
        produto.setFgc(request.getFgc());
    }
}