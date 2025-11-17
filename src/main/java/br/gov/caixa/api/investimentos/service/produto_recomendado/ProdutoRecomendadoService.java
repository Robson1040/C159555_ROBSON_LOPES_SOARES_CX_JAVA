package br.gov.caixa.api.investimentos.service.produto_recomendado;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import br.gov.caixa.api.investimentos.mapper.ProdutoMapper;
import br.gov.caixa.api.investimentos.repository.produto.IProdutoRepository;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import br.gov.caixa.api.investimentos.repository.simulacao.ISimulacaoInvestimentoRepository;
import br.gov.caixa.api.investimentos.service.cliente.ClienteService;
import br.gov.caixa.api.investimentos.ml.GeradorRecomendacaoML;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para recomendação de produtos baseado no perfil de risco
 */
@ApplicationScoped
public class ProdutoRecomendadoService {

    @Inject
    ProdutoMapper produtoMapper;

    @Inject
    IProdutoRepository produtoRepository;

    @Inject
    IInvestimentoRepository investimentoRepository;

    @Inject
    ISimulacaoInvestimentoRepository simulacaoRepository;

    @Inject
    ClienteService clienteService;

    @Inject
    GeradorRecomendacaoML geradorRecomendacaoML;

    /**
     * Busca produtos recomendados baseado no perfil de risco
     * 
     * @param perfil Perfil de risco (Conservador, Moderado, Agressivo)
     * @return Lista de produtos recomendados
     */
    public List<ProdutoResponse> buscarProdutosPorPerfil(String perfil) {
        if (perfil == null || perfil.trim().isEmpty()) {
            throw new IllegalArgumentException("Perfil não pode ser nulo ou vazio");
        }

        NivelRisco nivelRisco = mapearPerfilParaNivelRisco(perfil.trim());
        
        // Buscar todos os produtos e filtrar por nível de risco
        List<Produto> todosProdutos = produtoRepository.listAll();
        
        List<Produto> produtosFiltrados = todosProdutos.stream()
                .filter(produto -> produto.getRisco() == nivelRisco)
                .collect(Collectors.toList());

        return produtoMapper.toResponseList(produtosFiltrados);
    }

    /**
     * Busca produtos recomendados baseado no histórico do cliente
     * Método equivalente ao calcularPerfilRisco, retornando produtos_sugeridos
     * 
     * @param clienteId ID do cliente
     * @return Lista de produtos recomendados baseados no histórico
     */
    public List<ProdutoResponse> buscarProdutosPorCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente ID não pode ser nulo");
        }

        // Validar se o cliente existe
        clienteService.buscarPorId(clienteId);

        List<Produto> produtos_sugeridos = new ArrayList<>();
        List<Produto> produtos = produtoRepository.listAll();

        // Buscar histórico de investimentos
        List<Investimento> investimentos = investimentoRepository.findByClienteId(clienteId);
        List<SimulacaoInvestimento> simulacoes = simulacaoRepository.findByClienteId(clienteId);

        if (!investimentos.isEmpty()) {
            produtos_sugeridos = geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(investimentos, produtos);
        }

        if (produtos_sugeridos.isEmpty()) {
            produtos_sugeridos = geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(simulacoes, produtos);
        }

        if (produtos_sugeridos.isEmpty()) {
            throw new IllegalStateException("Cliente não possui histórico de investimentos nem simulações para gerar recomendações");
        }

        return produtoMapper.toResponseList(produtos_sugeridos);
    }

    /**
     * Mapeia o perfil de risco para o enum NivelRisco
     * 
     * @param perfil String do perfil (Conservador, Moderado, Agressivo)
     * @return NivelRisco correspondente
     */
    private NivelRisco mapearPerfilParaNivelRisco(String perfil) {
        return switch (perfil.toLowerCase()) {
            case "conservador" -> NivelRisco.BAIXO;
            case "moderado" -> NivelRisco.MEDIO;
            case "agressivo" -> NivelRisco.ALTO;
            default -> throw new IllegalArgumentException("Perfil inválido: " + perfil + ". Valores aceitos: Conservador, Moderado, Agressivo");
        };
    }
}

