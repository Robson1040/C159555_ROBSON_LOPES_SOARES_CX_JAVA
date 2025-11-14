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

        // Filtrar itens null que podem vir do Hibernate
        return produtos.stream()
                .filter(produto -> produto != null)
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
                request.nome(),
                request.tipo(),
                request.tipoRentabilidade(),
                request.rentabilidade(),
                request.periodoRentabilidade(),
                request.indice(),
                request.liquidez(),
                request.minimoDiasInvestimento(),
                request.fgc()
        );
    }

    /**
     * Atualiza uma entidade Produto existente com dados de um ProdutoRequest
     */
    public void updateEntityFromRequest(Produto produto, ProdutoRequest request) {
        if (produto == null || request == null) {
            return;
        }

        produto.setNome(request.nome());
        produto.setTipo(request.tipo());
        produto.setTipoRentabilidade(request.tipoRentabilidade());
        produto.setRentabilidade(request.rentabilidade());
        produto.setPeriodoRentabilidade(request.periodoRentabilidade());
        produto.setIndice(request.indice());
        produto.setLiquidez(request.liquidez());
        produto.setMinimoDiasInvestimento(request.minimoDiasInvestimento());
        produto.setFgc(request.fgc());
    }
}