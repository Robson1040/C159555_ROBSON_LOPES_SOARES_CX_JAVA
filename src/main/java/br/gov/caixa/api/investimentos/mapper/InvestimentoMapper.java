package br.gov.caixa.api.investimentos.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoResponse;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class InvestimentoMapper {

    /**
     * Converte uma entidade Investimento para InvestimentoResponse
     */
    public InvestimentoResponse toResponse(Investimento investimento) {
        if (investimento == null) {
            return null;
        }

        return new InvestimentoResponse(
                investimento.getId(),
                investimento.getClienteId(),
                investimento.getProdutoId(),
                investimento.getValor(),
                investimento.getPrazoMeses(),
                investimento.getPrazoDias(),
                investimento.getPrazoAnos(),
                investimento.getData(),
                investimento.getTipo(),
                investimento.getTipoRentabilidade(),
                investimento.getRentabilidade(),
                investimento.getPeriodoRentabilidade(),
                investimento.getIndice(),
                investimento.getLiquidez(),
                investimento.getMinimoDiasInvestimento(),
                investimento.getFgc()
        );
    }

    /**
     * Converte uma lista de entidades Investimento para uma lista de InvestimentoResponse
     */
    public List<InvestimentoResponse> toResponseList(List<Investimento> investimentos) {
        if (investimentos == null) {
            return null;
        }

        return investimentos.stream()
                .filter(investimento -> investimento != null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converte um InvestimentoRequest para entidade Investimento usando um Produto
     */
    public Investimento toEntity(InvestimentoRequest request, Produto produto) {
        if (request == null || produto == null) {
            return null;
        }

        Investimento inv = new Investimento();
        inv.setClienteId(request.clienteId());
        inv.setProdutoId(request.produtoId());
        inv.setValor(request.valor());
        inv.setPrazoMeses(request.prazoMeses());
        inv.setPrazoDias(request.prazoDias());
        inv.setPrazoAnos(request.prazoAnos());
        inv.setTipo(produto.getTipo());
        inv.setTipoRentabilidade(produto.getTipoRentabilidade());
        inv.setRentabilidade(produto.getRentabilidade());
        inv.setPeriodoRentabilidade(produto.getPeriodoRentabilidade());
        inv.setIndice(produto.getIndice());
        inv.setLiquidez(produto.getLiquidez());
        inv.setMinimoDiasInvestimento(produto.getMinimoDiasInvestimento());
        inv.setFgc(produto.getFgc());
        return inv;
    }

    /**
     * Atualiza uma entidade Investimento existente com dados de um InvestimentoRequest
     */
    public void updateEntityFromRequest(Investimento investimento, InvestimentoRequest request, Produto produto) {
        if (investimento == null || request == null || produto == null) {
            return;
        }

        investimento.setClienteId(request.clienteId());
        investimento.setProdutoId(request.produtoId());
        investimento.setValor(request.valor());
        investimento.setPrazoMeses(request.prazoMeses());
        investimento.setPrazoDias(request.prazoDias());
        investimento.setPrazoAnos(request.prazoAnos());
        
        // Atualizar informações do produto (snapshot)
        investimento.setTipo(produto.getTipo());
        investimento.setTipoRentabilidade(produto.getTipoRentabilidade());
        investimento.setRentabilidade(produto.getRentabilidade());
        investimento.setPeriodoRentabilidade(produto.getPeriodoRentabilidade());
        investimento.setIndice(produto.getIndice());
        investimento.setLiquidez(produto.getLiquidez());
        investimento.setMinimoDiasInvestimento(produto.getMinimoDiasInvestimento());
        investimento.setFgc(produto.getFgc());
    }
}
