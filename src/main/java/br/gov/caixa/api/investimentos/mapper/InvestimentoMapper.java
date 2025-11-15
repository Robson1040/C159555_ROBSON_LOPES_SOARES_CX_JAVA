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
                investimento.id,
                investimento.clienteId,
                investimento.produtoId,
                investimento.valor,
                investimento.prazoMeses,
                investimento.prazoDias,
                investimento.prazoAnos,
                investimento.data,
                investimento.tipo,
                investimento.tipoRentabilidade,
                investimento.rentabilidade,
                investimento.periodoRentabilidade,
                investimento.indice,
                investimento.liquidez,
                investimento.minimoDiasInvestimento,
                investimento.fgc
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

        return Investimento.from(request, produto);
    }

    /**
     * Atualiza uma entidade Investimento existente com dados de um InvestimentoRequest
     */
    public void updateEntityFromRequest(Investimento investimento, InvestimentoRequest request, Produto produto) {
        if (investimento == null || request == null || produto == null) {
            return;
        }

        investimento.clienteId = request.clienteId();
        investimento.produtoId = request.produtoId();
        investimento.valor = request.valor();
        investimento.prazoMeses = request.prazoMeses();
        investimento.prazoDias = request.prazoDias();
        investimento.prazoAnos = request.prazoAnos();
        investimento.data = request.data();
        
        // Atualizar informações do produto (snapshot)
        investimento.tipo = produto.getTipo();
        investimento.tipoRentabilidade = produto.getTipoRentabilidade();
        investimento.rentabilidade = produto.getRentabilidade();
        investimento.periodoRentabilidade = produto.getPeriodoRentabilidade();
        investimento.indice = produto.getIndice();
        investimento.liquidez = produto.getLiquidez();
        investimento.minimoDiasInvestimento = produto.getMinimoDiasInvestimento();
        investimento.fgc = produto.getFgc();
    }
}
