package br.gov.caixa.api.investimentos.ml;

import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class GeradorRecomendacaoML {

    public List<Produto> encontrarProdutosOrdenadosPorAparicao(List<?> entradas, List<Produto> todosProdutos) {
        if (entradas == null) {
            throw new IllegalArgumentException("Lista de entradas não pode ser nula");
        }
        if (todosProdutos == null) {
            throw new IllegalArgumentException("Lista de produtos não pode ser nula");
        }
        if (entradas.isEmpty() || todosProdutos.isEmpty()) {
            return List.of();
        }

        Map<Produto, Double> contador = new HashMap<>();

        for (Object entrada : entradas) {
            Produto produtoMaisProximo = null;
            double menorDistancia = Double.MAX_VALUE;
            double pesoBase = 0;
            double decayFactor = 1.0;
            double peso = 0;

            if (entrada instanceof Investimento investimento) {
                pesoBase = (int) (Math.log10(investimento.getValor().doubleValue() + 1) * 1000);
                if (investimento.getData() != null) {
                    long diasDesdeInvestimento = ChronoUnit.DAYS.between(investimento.getData(), LocalDate.now());
                    decayFactor = Math.exp(-diasDesdeInvestimento / 365.0);
                }
                peso = (int) (pesoBase * decayFactor);
            } else if (entrada instanceof SimulacaoInvestimento simulacao) {
                pesoBase = (Math.log10(simulacao.getValorInvestido().doubleValue() + 1) * 100);

                double diasDesdeSimulacao = ChronoUnit.DAYS.between(simulacao.getDataSimulacao().toLocalDate(), LocalDate.now());
                decayFactor = Math.exp(-diasDesdeSimulacao / 365.0);
                peso = (pesoBase * decayFactor);
            } else {
                throw new IllegalArgumentException("Tipo não suportado: " + entrada.getClass());
            }

            for (Produto produto : todosProdutos) {
                Long produtoId = null;
                if (entrada instanceof Investimento investimento) {
                    produtoId = investimento.getProdutoId();
                } else if (entrada instanceof SimulacaoInvestimento simulacao) {
                    produtoId = simulacao.getProdutoId();
                }
                if (produtoId != null && produtoId.equals(produto.getId())) {
                    continue;
                }

                double distancia = calcularDistanciaEuclidiana(entrada, produto, todosProdutos);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    produtoMaisProximo = produto;
                }
            }

            if (produtoMaisProximo != null) {

                produtoMaisProximo.setPontuacao(produtoMaisProximo.getPontuacao() + peso);
                contador.merge(produtoMaisProximo, peso, Double::sum);
            }
        }

        return contador.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private double calcularDistanciaEuclidiana(Object entrada, Produto produto, List<Produto> todoProdutos) {
        double valorNorm;
        double tipoNorm;
        double tipoRentNorm;
        double periodoRentNorm;
        double indiceNorm;
        double liquidezNorm;
        double fgcNorm;
        double minimoInvNorm;

        if (entrada instanceof Investimento investimento) {
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
        } else if (entrada instanceof SimulacaoInvestimento simulacao) {
            valorNorm = normalizar(simulacao.getValorInvestido().doubleValue(), 0, 1_000_000);

            Produto p = null;

            for (Produto todoProduto : todoProdutos) {
                if (Objects.equals(simulacao.getProdutoId(), todoProduto.getId())) {
                    p = todoProduto;
                    break;
                }
            }

            if (p == null) {
                throw new IllegalArgumentException(
                        "Produto não encontrado para simulação com produtoId: " + simulacao.getProdutoId()
                );
            }

            tipoNorm = p.getTipo().getValor();

            tipoRentNorm = normalizarTipoRentabilidade(p.getTipoRentabilidade());
            periodoRentNorm = normalizarPeriodoRentabilidade(p.getPeriodoRentabilidade());
            indiceNorm = normalizarIndice(p.getIndice());
            liquidezNorm = normalizar(
                    p.getLiquidez() != null ? p.getLiquidez() : 0,
                    -1, 365
            );
            fgcNorm = p.getFgc() != null && p.getFgc() ? 1.0 : 0.0;
            minimoInvNorm = normalizar(
                    p.getMinimoDiasInvestimento() != null ? p.getMinimoDiasInvestimento() : 0,
                    0, 1800
            );
        } else {
            throw new IllegalArgumentException(
                    "Tipo não suportado: " + entrada.getClass()
            );
        }

        double prodValorNorm = produto.getRentabilidade() != null ?
                normalizar(produto.getRentabilidade().doubleValue() * 10000, 0, 1_000_000) : 0.5;
        double prodTipoNorm = normalizarTipoProduto(produto.getTipo());
        double prodTipoRentNorm = normalizarTipoRentabilidade(produto.getTipoRentabilidade());
        double prodPeriodoRentNorm = normalizarPeriodoRentabilidade(produto.getPeriodoRentabilidade());
        double prodIndiceNorm = normalizarIndice(produto.getIndice());
        double prodLiquidezNorm = normalizar(produto.getLiquidez(), -1, 365);
        double prodFgcNorm = produto.getFgc() ? 1.0 : 0.0;
        double prodMinimoInvNorm = normalizar(produto.getMinimoDiasInvestimento(), 0, 1800);

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

    private double normalizar(double valor, double min, double max) {
        if (max == min) return 0.5;
        return Math.max(0, Math.min(1, (valor - min) / (max - min)));
    }

    private double normalizarTipoProduto(TipoProduto tipo) {
        return tipo.getValor();
    }

    private double normalizarTipoRentabilidade(TipoRentabilidade tipo) {
        if (tipo == null) return 0.5;
        return tipo == TipoRentabilidade.PRE ? 0.0 : 1.0;
    }

    private double normalizarPeriodoRentabilidade(PeriodoRentabilidade periodo) {
        if (periodo == null) return 0.5;
        return switch (periodo) {
            case AO_DIA -> 0.0;
            case AO_MES -> 0.33;
            case AO_ANO -> 0.66;
            case PERIODO_TOTAL -> 1.0;
        };
    }

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

}
