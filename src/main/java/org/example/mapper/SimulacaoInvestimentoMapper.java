package org.example.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.dto.simulacao.ResultadoSimulacao;
import org.example.dto.simulacao.SimulacaoInvestimentoResponse;
import org.example.model.SimulacaoInvestimento;

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
                simulacao.id,
                simulacao.clienteId,
                simulacao.produto,
                simulacao.valorInvestido,
                simulacao.valorFinal,
                simulacao.prazoMeses,
                simulacao.prazoDias,
                simulacao.prazoAnos,
                simulacao.dataSimulacao,
                simulacao.rentabilidadeEfetiva,
                simulacao.rendimento,
                simulacao.valorSimulado,
                simulacao.cenarioSimulacao
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
    public SimulacaoInvestimento toEntity(Long clienteId, String nomeProduto, BigDecimal valorInvestido, ResultadoSimulacao resultado) {
        if (clienteId == null || nomeProduto == null || valorInvestido == null || resultado == null) {
            return null;
        }

        return SimulacaoInvestimento.fromSimulacao(clienteId, nomeProduto, valorInvestido, resultado);
    }

    /**
     * Cria uma entidade SimulacaoInvestimento com dados básicos
     */
    public SimulacaoInvestimento toEntity(Long clienteId, String produto, BigDecimal valorInvestido, 
                                        BigDecimal valorFinal, Integer prazoMeses, Integer prazoDias, 
                                        Integer prazoAnos) {
        if (clienteId == null || produto == null || valorInvestido == null || valorFinal == null) {
            return null;
        }

        return new SimulacaoInvestimento(
                clienteId,
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

        simulacao.clienteId = clienteId;
        simulacao.produto = produto;
        simulacao.valorInvestido = valorInvestido;
        simulacao.valorFinal = valorFinal;
        simulacao.prazoMeses = prazoMeses;
        simulacao.prazoDias = prazoDias;
        simulacao.prazoAnos = prazoAnos;
        simulacao.rentabilidadeEfetiva = rentabilidadeEfetiva;
        simulacao.rendimento = rendimento;
        simulacao.valorSimulado = valorSimulado;
        simulacao.cenarioSimulacao = cenarioSimulacao;
    }
}