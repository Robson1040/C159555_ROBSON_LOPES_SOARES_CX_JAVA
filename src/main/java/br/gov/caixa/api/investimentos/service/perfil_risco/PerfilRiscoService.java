package br.gov.caixa.api.investimentos.service.perfil_risco;

import br.gov.caixa.api.investimentos.ml.GeradorRecomendacaoML;
import br.gov.caixa.api.investimentos.repository.produto.IProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.gov.caixa.api.investimentos.dto.perfil_risco.PerfilRiscoResponse;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import br.gov.caixa.api.investimentos.repository.simulacao.ISimulacaoInvestimentoRepository;
import br.gov.caixa.api.investimentos.service.cliente.ClienteService;

import java.util.*;

/**
 * Serviço responsável pelo cálculo do perfil de risco do cliente
 * baseado no histórico de investimentos ou simulações
 */
@ApplicationScoped
public class PerfilRiscoService {

    @Inject
    ClienteService clienteService;

    @Inject
    IInvestimentoRepository investimentoRepository;

    @Inject
    ISimulacaoInvestimentoRepository simulacaoRepository;

    @Inject
    IProdutoRepository produtoRepository;

    @Inject
    GeradorRecomendacaoML geradorRecomendacaoML;
    /**
     * Calcula o perfil de risco de um cliente
     */
    public PerfilRiscoResponse calcularPerfilRisco(Long clienteId) {
        // 1. Validar se o cliente existe
        validarCliente(clienteId);

        List<Produto> produtos_sugeridos = new ArrayList<>();
        List<Produto> produtos = produtoRepository.listAll();

        // 2. Buscar histórico de investimentos
        List<Investimento> investimentos = investimentoRepository.findByClienteId(clienteId);
        List<SimulacaoInvestimento> simulacoes = simulacaoRepository.findByClienteId(clienteId);

        if (!investimentos.isEmpty())
        {
            produtos_sugeridos = geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(investimentos, produtos);
        }

        if (produtos_sugeridos.isEmpty())
        {
            produtos_sugeridos = geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicaoSimulacao(simulacoes, produtos);
        }

        if (produtos_sugeridos.isEmpty())
        {
            throw new IllegalStateException("Cliente não possui histórico de investimentos nem simulações para calcular perfil de risco");
        }



        return determinarPerfilFinal(clienteId, contarNivelRisco(produtos_sugeridos, produtos_sugeridos.getFirst().getRisco()), contarTotal(produtos_sugeridos), produtos_sugeridos.getFirst());
    }

    /**
     * Valida se o cliente existe no sistema
     */
    private void validarCliente(Long clienteId)
    {
        try {
            clienteService.buscarPorId(clienteId);
        } catch (ClienteNotFoundException e) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + clienteId);
        }
    }

    /**
     * Determina o perfil final baseado na contagem de riscos
     */

    private int contarNivelRisco(List<Produto> produtos, NivelRisco nivel)
    {
        int quantidade = 0;

        for (Produto produto : produtos) {
            if (produto.getRisco().equals(nivel)) {
                quantidade += produto.getPontuacao();
            }
        }

        return quantidade;
    }

    private int contarTotal(List<Produto> produtos)
    {
        int quantidade = 0;

        for (Produto produto : produtos)
        {
            quantidade += produto.getPontuacao();
        }

        return quantidade;
    }

    private PerfilRiscoResponse determinarPerfilFinal(Long clienteId, int aparicoes, int total, Produto produto)
    {


        int pontuacao = total == 0 ? 0 : (aparicoes * 100) / total;

        // Determinar perfil pela maioria
        if (produto.getRisco().equals(NivelRisco.BAIXO))
        {
            return PerfilRiscoResponse.conservador(clienteId, pontuacao);
        }
        else if (produto.getRisco().equals(NivelRisco.MEDIO))
        {
            return PerfilRiscoResponse.moderado(clienteId, pontuacao);
        }
        else
        {
            return PerfilRiscoResponse.agressivo(clienteId, pontuacao);
        }
    }
}
