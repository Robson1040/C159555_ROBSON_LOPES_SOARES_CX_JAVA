package br.gov.caixa.api.investimentos.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.model.produto.Produto;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoMapper {

    
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

    
    public List<ProdutoResponse> toResponseList(List<Produto> produtos) {
        if (produtos == null) {
            return null;
        }

        
        return produtos.stream()
                .filter(produto -> produto != null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    
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
