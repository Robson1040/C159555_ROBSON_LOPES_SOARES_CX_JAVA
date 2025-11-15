package br.gov.caixa.api.investimentos.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.dto.simulacao.ResultadoSimulacao;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoInvestimentoResponse;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SimulacaoInvestimentoMapper {

    /**
     * Converte uma entidade SimulacaoInvestimento para SimulacaoInvestimentoResponse
     */
    public SimulacaoInvestimentoResponse toResponse(SimulacaoInvestimento simulacao) {
        if (simulacao == null) {
            return null;
        }

        return new SimulacaoInvestimentoResponse(
                simulacao.getId(),
                simulacao.getProdutoId(),
                simulacao.getClienteId(),
                simulacao.getProduto(),
                simulacao.getValorInvestido(),
                simulacao.getValorFinal(),
                simulacao.getPrazoMeses(),
                simulacao.getPrazoDias(),
                simulacao.getPrazoAnos(),
                simulacao.getDataSimulacao(),
                simulacao.getRentabilidadeEfetiva(),
                simulacao.getRendimento(),
                simulacao.getValorSimulado(),
                simulacao.getCenarioSimulacao()
        );
    }

    /**
     * Converte uma lista de entidades SimulacaoInvestimento para uma lista de SimulacaoInvestimentoResponse
     */
    public List<SimulacaoInvestimentoResponse> toResponseList(List<SimulacaoInvestimento> simulacoes) {
        if (simulacoes == null) {
            return null;
        }

        return simulacoes.stream()
                .filter(simulacao -> simulacao != null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converte um SimulacaoRequest e ResultadoSimulacao para entidade SimulacaoInvestimento
     */
    public SimulacaoInvestimento toEntity(Long clienteId, Long produtoId, String nomeProduto, BigDecimal valorInvestido, ResultadoSimulacao resultado) {
        if (clienteId == null || nomeProduto == null || valorInvestido == null || resultado == null) {
            return null;
        }

        return new SimulacaoInvestimento(
                clienteId,
                produtoId,
                nomeProduto,
                valorInvestido,
                resultado.valorFinal(),
                resultado.prazoMeses(),
                resultado.prazoDias(),
                resultado.prazoAnos(),
                resultado.rentabilidadeEfetiva(),
                resultado.rendimento(),
                resultado.valorSimulado(),
                resultado.cenarioSimulacao()
        );
    }

    /**
     * Cria uma entidade SimulacaoInvestimento com dados básicos
     */
    public SimulacaoInvestimento toEntity(Long clienteId, Long produtoId, String produto, BigDecimal valorInvestido,
                                        BigDecimal valorFinal, Integer prazoMeses, Integer prazoDias, 
                                        Integer prazoAnos) {
        if (clienteId == null || produto == null || valorInvestido == null || valorFinal == null) {
            return null;
        }

        return new SimulacaoInvestimento(
                clienteId,
                produtoId,
                produto,
                valorInvestido,
                valorFinal,
                prazoMeses,
                prazoDias,
                prazoAnos
        );
    }

    /**
     * Atualiza uma entidade SimulacaoInvestimento existente
     */
    public void updateEntityFromData(SimulacaoInvestimento simulacao, Long clienteId, String produto, 
                                   BigDecimal valorInvestido, BigDecimal valorFinal, 
                                   Integer prazoMeses, Integer prazoDias, Integer prazoAnos,
                                   BigDecimal rentabilidadeEfetiva, BigDecimal rendimento, 
                                   Boolean valorSimulado, String cenarioSimulacao) {
        if (simulacao == null) {
            return;
        }

        simulacao.setClienteId(clienteId);
        simulacao.setProduto(produto);
        simulacao.setValorInvestido(valorInvestido);
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(prazoMeses);
        simulacao.setPrazoDias(prazoDias);
        simulacao.setPrazoAnos(prazoAnos);
        simulacao.setRentabilidadeEfetiva(rentabilidadeEfetiva);
        simulacao.setRendimento(rendimento);
        simulacao.setValorSimulado(valorSimulado);
        simulacao.setCenarioSimulacao(cenarioSimulacao);
    }
}
