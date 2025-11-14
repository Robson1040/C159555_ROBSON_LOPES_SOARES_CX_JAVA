package org.example.service.perfil_risco;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.perfil_risco.PerfilRiscoResponse;
import org.example.enums.produto.NivelRisco;
import org.example.enums.produto.PeriodoRentabilidade;
import org.example.enums.produto.TipoProduto;
import org.example.enums.produto.TipoRentabilidade;
import org.example.enums.simulacao.Indice;
import org.example.exception.ClienteNotFoundException;
import org.example.model.investimento.Investimento;
import org.example.model.produto.Produto;
import org.example.model.simulacao.SimulacaoInvestimento;
import org.example.repository.investimento.IInvestimentoRepository;
import org.example.repository.simulacao.ISimulacaoInvestimentoRepository;
import org.example.service.cliente.ClienteService;

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

    /**
     * Calcula o perfil de risco de um cliente
     */
    public PerfilRiscoResponse calcularPerfilRisco(Long clienteId) {
        // 1. Validar se o cliente existe
        validarCliente(clienteId);

        // 2. Buscar histórico de investimentos
        List<Investimento> investimentos = investimentoRepository.findByClienteId(clienteId);
        
        if (!investimentos.isEmpty()) {
            return calcularPerfilPorInvestimentos(clienteId, investimentos);
        }

        // 3. Se não há investimentos, usar simulações
        List<SimulacaoInvestimento> simulacoes = simulacaoRepository.findByClienteId(clienteId);
        
        if (!simulacoes.isEmpty()) {
            return calcularPerfilPorSimulacoes(clienteId, simulacoes);
        }

        // 4. Se não há histórico nenhum, retornar erro
        throw new IllegalStateException("Cliente não possui histórico de investimentos nem simulações para calcular perfil de risco");
    }

    /**
     * Valida se o cliente existe no sistema
     */
    private void validarCliente(Long clienteId) {
        try {
            clienteService.buscarPorId(clienteId);
        } catch (ClienteNotFoundException e) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + clienteId);
        }
    }

    /**
     * Calcula perfil baseado no histórico de investimentos
     */
    private PerfilRiscoResponse calcularPerfilPorInvestimentos(Long clienteId, List<Investimento> investimentos) {
        // Buscar todos os produtos disponíveis para comparação
        List<Produto> todosProdutos = Produto.listAll();
        
        // Calcular risco para cada investimento
        Map<NivelRisco, Integer> contadorRiscos = new HashMap<>();
        contadorRiscos.put(NivelRisco.BAIXO, 0);
        contadorRiscos.put(NivelRisco.MEDIO, 0);
        contadorRiscos.put(NivelRisco.ALTO, 0);

        for (Investimento investimento : investimentos) {
            NivelRisco riscoMaisProximo = encontrarRiscoMaisProximo(investimento, todosProdutos);
            contadorRiscos.merge(riscoMaisProximo, 1, Integer::sum);
        }

        return determinarPerfilFinal(clienteId, contadorRiscos, investimentos.size());
    }

    /**
     * Calcula perfil baseado no histórico de simulações
     */
    private PerfilRiscoResponse calcularPerfilPorSimulacoes(Long clienteId, List<SimulacaoInvestimento> simulacoes) {
        // Buscar todos os produtos disponíveis para comparação
        List<Produto> todosProdutos = Produto.listAll();
        
        // Calcular risco para cada simulação
        Map<NivelRisco, Integer> contadorRiscos = new HashMap<>();
        contadorRiscos.put(NivelRisco.BAIXO, 0);
        contadorRiscos.put(NivelRisco.MEDIO, 0);
        contadorRiscos.put(NivelRisco.ALTO, 0);

        for (SimulacaoInvestimento simulacao : simulacoes) {
            NivelRisco riscoMaisProximo = encontrarRiscoMaisProximoSimulacao(simulacao, todosProdutos);
            contadorRiscos.merge(riscoMaisProximo, 1, Integer::sum);
        }

        return determinarPerfilFinal(clienteId, contadorRiscos, simulacoes.size());
    }

    /**
     * Encontra o produto com menor distância euclidiana para um investimento
     */
    private NivelRisco encontrarRiscoMaisProximo(Investimento investimento, List<Produto> todosProdutos) {
        double menorDistancia = Double.MAX_VALUE;
        NivelRisco riscoMaisProximo = NivelRisco.MEDIO;

        for (Produto produto : todosProdutos) {
            double distancia = calcularDistanciaEuclidiana(investimento, produto);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                riscoMaisProximo = produto.getRisco();
            }
        }

        return riscoMaisProximo;
    }

    /**
     * Encontra o produto com menor distância euclidiana para uma simulação
     */
    private NivelRisco encontrarRiscoMaisProximoSimulacao(SimulacaoInvestimento simulacao, List<Produto> todosProdutos) {
        double menorDistancia = Double.MAX_VALUE;
        NivelRisco riscoMaisProximo = NivelRisco.MEDIO;

        for (Produto produto : todosProdutos) {
            double distancia = calcularDistanciaEuclidianaSimulacao(simulacao, produto);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                riscoMaisProximo = produto.getRisco();
            }
        }

        return riscoMaisProximo;
    }

    /**
     * Calcula distância euclidiana entre investimento e produto
     */
    private double calcularDistanciaEuclidiana(Investimento investimento, Produto produto) {
        // Normalizar valores para o cálculo
        double valorNorm = normalizar(investimento.valor.doubleValue(), 0, 1000000);
        double tipoNorm = normalizarTipoProduto(investimento.tipo);
        double tipoRentNorm = normalizarTipoRentabilidade(investimento.tipoRentabilidade);
        double periodoRentNorm = normalizarPeriodoRentabilidade(investimento.periodoRentabilidade);
        double indiceNorm = normalizarIndice(investimento.indice);
        double liquidezNorm = normalizar(investimento.liquidez != null ? investimento.liquidez : 0, -1, 365);
        double fgcNorm = investimento.fgc != null && investimento.fgc ? 1.0 : 0.0;
        double minimoInvNorm = normalizar(investimento.minimoDiasInvestimento != null ? investimento.minimoDiasInvestimento : 0, 0, 1800);

        // Valores do produto para comparação
        double prodValorNorm = 0.5; // Valor neutro para produtos
        double prodTipoNorm = normalizarTipoProduto(produto.getTipo());
        double prodTipoRentNorm = normalizarTipoRentabilidade(produto.getTipoRentabilidade());
        double prodPeriodoRentNorm = normalizarPeriodoRentabilidade(produto.getPeriodoRentabilidade());
        double prodIndiceNorm = normalizarIndice(produto.getIndice());
        double prodLiquidezNorm = normalizar(produto.getLiquidez(), -1, 365);
        double prodFgcNorm = produto.getFgc() ? 1.0 : 0.0;
        double prodMinimoInvNorm = normalizar(produto.getMinimoDiasInvestimento(), 0, 1800);

        // Calcular distância euclidiana
        return Math.sqrt(
                Math.pow(valorNorm - prodValorNorm, 2) +
                Math.pow(tipoNorm - prodTipoNorm, 2) +
                Math.pow(tipoRentNorm - prodTipoRentNorm, 2) +
                Math.pow(periodoRentNorm - prodPeriodoRentNorm, 2) +
                Math.pow(indiceNorm - prodIndiceNorm, 2) +
                Math.pow(liquidezNorm - prodLiquidezNorm, 2) +
                Math.pow(fgcNorm - prodFgcNorm, 2) +
                Math.pow(minimoInvNorm - prodMinimoInvNorm, 2)
        );
    }

    /**
     * Calcula distância euclidiana entre simulação e produto
     */
    private double calcularDistanciaEuclidianaSimulacao(SimulacaoInvestimento simulacao, Produto produto) {
        // Normalizar valores para o cálculo
        double valorNorm = normalizar(simulacao.valorInvestido.doubleValue(), 0, 1000000);
        
        // Para simulações, usamos valores baseados no nome do produto
        double tipoNorm = inferirTipoProdutoDeNome(simulacao.produto);
        double tipoRentNorm = 0.5; // Valor neutro para simulações
        double periodoRentNorm = 0.5; // Valor neutro para simulações
        double indiceNorm = 0.5; // Valor neutro para simulações
        double liquidezNorm = 0.5; // Valor neutro para simulações
        double fgcNorm = inferirFgcDeNome(simulacao.produto);
        double minimoInvNorm = 0.5; // Valor neutro para simulações

        // Valores do produto para comparação
        double prodValorNorm = 0.5; // Valor neutro para produtos
        double prodTipoNorm = normalizarTipoProduto(produto.getTipo());
        double prodTipoRentNorm = normalizarTipoRentabilidade(produto.getTipoRentabilidade());
        double prodPeriodoRentNorm = normalizarPeriodoRentabilidade(produto.getPeriodoRentabilidade());
        double prodIndiceNorm = normalizarIndice(produto.getIndice());
        double prodLiquidezNorm = normalizar(produto.getLiquidez(), -1, 365);
        double prodFgcNorm = produto.getFgc() ? 1.0 : 0.0;
        double prodMinimoInvNorm = normalizar(produto.getMinimoDiasInvestimento(), 0, 1800);

        // Calcular distância euclidiana
        return Math.sqrt(
                Math.pow(valorNorm - prodValorNorm, 2) +
                Math.pow(tipoNorm - prodTipoNorm, 2) +
                Math.pow(tipoRentNorm - prodTipoRentNorm, 2) +
                Math.pow(periodoRentNorm - prodPeriodoRentNorm, 2) +
                Math.pow(indiceNorm - prodIndiceNorm, 2) +
                Math.pow(liquidezNorm - prodLiquidezNorm, 2) +
                Math.pow(fgcNorm - prodFgcNorm, 2) +
                Math.pow(minimoInvNorm - prodMinimoInvNorm, 2)
        );
    }

    /**
     * Normaliza valores para escala 0-1
     */
    private double normalizar(double valor, double min, double max) {
        if (max == min) return 0.5;
        return Math.max(0, Math.min(1, (valor - min) / (max - min)));
    }

    /**
     * Normaliza tipo de produto para valor numérico
     */
    private double normalizarTipoProduto(TipoProduto tipo) {
        if (tipo == null) return 0.5;
        return switch (tipo) {
            case POUPANCA -> 0.0;
            case CDB, LCI, LCA -> 0.2;
            case TESOURO_DIRETO -> 0.4;
            case FUNDO -> 1.0;
        };
    }

    /**
     * Normaliza tipo de rentabilidade para valor numérico
     */
    private double normalizarTipoRentabilidade(TipoRentabilidade tipo) {
        if (tipo == null) return 0.5;
        return tipo == TipoRentabilidade.PRE ? 0.0 : 1.0;
    }

    /**
     * Normaliza período de rentabilidade para valor numérico
     */
    private double normalizarPeriodoRentabilidade(PeriodoRentabilidade periodo) {
        if (periodo == null) return 0.5;
        return switch (periodo) {
            case AO_DIA -> 0.0;
            case AO_MES -> 0.33;
            case AO_ANO -> 0.66;
            case PERIODO_TOTAL -> 1.0;
        };
    }

    /**
     * Normaliza índice para valor numérico
     */
    private double normalizarIndice(Indice indice) {
        if (indice == null || indice == Indice.NENHUM) return 0.0;
        return switch (indice) {
            case SELIC -> 0.2;
            case CDI -> 0.4;
            case IPCA -> 0.6;
            case IGP_M -> 0.8;
            case IBOVESPA -> 1.0;
            default -> 0.0;
        };
    }

    /**
     * Infere tipo de produto baseado no nome da simulação
     */
    private double inferirTipoProdutoDeNome(String nomeProduto) {
        String nome = nomeProduto.toUpperCase();
        if (nome.contains("CDB")) return normalizarTipoProduto(TipoProduto.CDB);
        if (nome.contains("LCI")) return normalizarTipoProduto(TipoProduto.LCI);
        if (nome.contains("LCA")) return normalizarTipoProduto(TipoProduto.LCA);
        if (nome.contains("TESOURO")) return normalizarTipoProduto(TipoProduto.TESOURO_DIRETO);
        if (nome.contains("FUNDO")) return normalizarTipoProduto(TipoProduto.FUNDO);
        if (nome.contains("POUPANÇA") || nome.contains("POUPANCA")) return normalizarTipoProduto(TipoProduto.POUPANCA);
        return 0.5; // Valor neutro se não conseguir inferir
    }

    /**
     * Infere se tem FGC baseado no nome da simulação
     */
    private double inferirFgcDeNome(String nomeProduto) {
        String nome = nomeProduto.toUpperCase();
        // Produtos que geralmente têm FGC
        if (nome.contains("CDB") || nome.contains("LCI") || nome.contains("LCA") || nome.contains("POUPANÇA") || nome.contains("POUPANCA")) {
            return 1.0;
        }
        // Produtos que geralmente não têm FGC
        if (nome.contains("FUNDO") || nome.contains("TESOURO")) {
            return 0.0;
        }
        return 0.5; // Valor neutro se não conseguir inferir
    }

    /**
     * Determina o perfil final baseado na contagem de riscos
     */
    private PerfilRiscoResponse determinarPerfilFinal(Long clienteId, Map<NivelRisco, Integer> contadorRiscos, int total) {
        int baixo = contadorRiscos.get(NivelRisco.BAIXO);
        int medio = contadorRiscos.get(NivelRisco.MEDIO);
        int alto = contadorRiscos.get(NivelRisco.ALTO);

        // Determinar perfil pela maioria
        if (baixo >= medio && baixo >= alto) {
            // Conservador - pontuação 0-33
            int pontuacao = calcularPontuacao(baixo, medio, alto, total, 0, 33);
            return PerfilRiscoResponse.conservador(clienteId, pontuacao);
        } else if (medio >= baixo && medio >= alto) {
            // Moderado - pontuação 34-66
            int pontuacao = calcularPontuacao(baixo, medio, alto, total, 34, 66);
            return PerfilRiscoResponse.moderado(clienteId, pontuacao);
        } else {
            // Agressivo - pontuação 67-100
            int pontuacao = calcularPontuacao(baixo, medio, alto, total, 67, 100);
            return PerfilRiscoResponse.agressivo(clienteId, pontuacao);
        }
    }

    /**
     * Calcula pontuação dentro da faixa do perfil
     */
    private int calcularPontuacao(int baixo, int medio, int alto, int total, int minFaixa, int maxFaixa) {
        // Calcular proporções
        double propBaixo = (double) baixo / total;
        double propMedio = (double) medio / total;
        double propAlto = (double) alto / total;

        // Calcular pontuação baseada nas proporções
        double pontuacaoBase = (propBaixo * 0) + (propMedio * 50) + (propAlto * 100);
        
        // Normalizar para a faixa específica do perfil
        int faixaRange = maxFaixa - minFaixa;
        int pontuacao = minFaixa + (int) Math.round((pontuacaoBase / 100.0) * faixaRange);
        
        // Garantir que está dentro da faixa
        return Math.max(minFaixa, Math.min(maxFaixa, pontuacao));
    }
}
