package br.gov.caixa.api.investimentos.ml;

import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class GeradorRecomendacaoML
{
    /**
     * Encontra o produto com perfil ideal para o cliente, com base nos investimentos
     */
    public List<Produto> encontrarProdutosOrdenadosPorAparicao(List<Investimento> investimentos,
                                                               List<Produto> todosProdutos)
    {
        Map<Produto, Integer> contador = new HashMap<>();

        for (Investimento investimento : investimentos) {
            Produto produtoMaisProximo = null;
            double menorDistancia = Double.MAX_VALUE;
            int peso = investimento.getValor().intValue();

            for (Produto produto : todosProdutos) {
                // IGNORA o "mesmo produto"
                if (investimento.getProdutoId().equals(produto.getId())) {
                    continue;
                }

                double distancia = calcularDistanciaEuclidiana(investimento, produto, todosProdutos);


                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    produtoMaisProximo = produto;
                }
            }

            if (produtoMaisProximo != null)
            {
                produtoMaisProximo.setPontuacao(produtoMaisProximo.getPontuacao() + peso);
                contador.merge(produtoMaisProximo, peso, Integer::sum);
            }
        }

        // Retorna os produtos ordenados pela contagem (decrescente)
        return contador.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Calcula distância euclidiana entre investimento e produto
     */
    public List<Produto> encontrarProdutosOrdenadosPorAparicaoSimulacao(
            List<SimulacaoInvestimento> investimentos,
            List<Produto> todosProdutos)
    {
        Map<Produto, Integer> contador = new HashMap<>();

        for (SimulacaoInvestimento simulacao : investimentos) {
            Produto produtoMaisProximo = null;
            double menorDistancia = Double.MAX_VALUE;
            int peso = simulacao.getValorInvestido().intValue();

            for (Produto produto : todosProdutos) {
                // IGNORA o "mesmo produto"
                if (simulacao.getProdutoId().equals(produto.getId())) {
                    continue;
                }

                double distancia = calcularDistanciaEuclidiana(simulacao, produto, todosProdutos);


                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    produtoMaisProximo = produto;
                }
            }

            if (produtoMaisProximo != null) {
                produtoMaisProximo.setPontuacao(produtoMaisProximo.getPontuacao() + peso);
                contador.merge(produtoMaisProximo, peso, Integer::sum);
            }
        }

        // Retorna os produtos ordenados pela contagem (decrescente)
        return contador.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Calcula distância euclidiana entre simulação e produto
     */
    private double calcularDistanciaEuclidiana(Object entrada, Produto produto, List<Produto> todoProdutos)
    {
        double valorNorm;
        double tipoNorm;
        double tipoRentNorm;
        double periodoRentNorm;
        double indiceNorm;
        double liquidezNorm;
        double fgcNorm;
        double minimoInvNorm;

        // === CASO 1: Investimento real ===
        if (entrada instanceof Investimento investimento)
        {
            valorNorm = normalizar(investimento.getValor().doubleValue(), 0, 1_000_000);
            tipoNorm = normalizarTipoProduto(investimento.getTipo());
            tipoRentNorm = normalizarTipoRentabilidade(investimento.getTipoRentabilidade());
            periodoRentNorm = normalizarPeriodoRentabilidade(investimento.getPeriodoRentabilidade());
            indiceNorm = normalizarIndice(investimento.getIndice());
            liquidezNorm = normalizar(
                    investimento.getLiquidez() != null ? investimento.getLiquidez() : 0,
                    -1, 365
            );
            fgcNorm = investimento.getFgc() != null && investimento.getFgc() ? 1.0 : 0.0;
            minimoInvNorm = normalizar(
                    investimento.getMinimoDiasInvestimento() != null ? investimento.getMinimoDiasInvestimento() : 0,
                    0, 1800
            );
        }

        // === CASO 2: Simulação ===
        else if (entrada instanceof SimulacaoInvestimento simulacao)
        {
            valorNorm = normalizar(simulacao.getValorInvestido().doubleValue(), 0, 1_000_000);

            Produto p = null;

            for (Produto todoProduto : todoProdutos) {
                if (Objects.equals(simulacao.getProdutoId(), todoProduto.getId()))
                {
                    p = todoProduto;
                    break;
                }
            }

            if(p == null)
            {
                throw new IllegalArgumentException(
                        "Produto não encontrado para simulação com produtoId: " + simulacao.getProdutoId()
                );
            }

            tipoNorm = p.getTipo().getValor();
            tipoRentNorm = 0.5;
            periodoRentNorm = 0.5;
            indiceNorm = 0.5;
            liquidezNorm = 0.5;
            fgcNorm = p.getFgc() != null && p.getFgc() ? 1.0 : 0.0;
            minimoInvNorm = 0.5;
        }

        else
        {
            throw new IllegalArgumentException(
                    "Tipo não suportado: " + entrada.getClass()
            );
        }

        // === Valores normais do produto ===
        double prodValorNorm = 0.5;
        double prodTipoNorm = normalizarTipoProduto(produto.getTipo());
        double prodTipoRentNorm = normalizarTipoRentabilidade(produto.getTipoRentabilidade());
        double prodPeriodoRentNorm = normalizarPeriodoRentabilidade(produto.getPeriodoRentabilidade());
        double prodIndiceNorm = normalizarIndice(produto.getIndice());
        double prodLiquidezNorm = normalizar(produto.getLiquidez(), -1, 365);
        double prodFgcNorm = produto.getFgc() ? 1.0 : 0.0;
        double prodMinimoInvNorm = normalizar(produto.getMinimoDiasInvestimento(), 0, 1800);

        // === Distância final ===
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
        return tipo.getValor();
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
}
