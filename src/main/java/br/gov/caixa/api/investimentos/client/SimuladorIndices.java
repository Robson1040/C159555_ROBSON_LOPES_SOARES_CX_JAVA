package br.gov.caixa.api.investimentos.client;

import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/* SIMULA INDICES DE MERCADO
 * COMO SE FOSSE UMA API EXTERNA */
@ApplicationScoped
public class SimuladorIndices {

    private final Random random = new Random();

    public BigDecimal getTaxaSimulada(Indice indice, int prazoMeses) {
        BigDecimal taxaBase = getTaxaBase(indice);
        BigDecimal variacao = calcularVariacaoPorPeriodo(indice, prazoMeses);

        return taxaBase.add(variacao).max(BigDecimal.ZERO);
    }

    private BigDecimal getTaxaBase(Indice indice) {
        return switch (indice) {
            case SELIC -> new BigDecimal("10.75");
            case CDI -> new BigDecimal("10.65");
            case IBOVESPA -> new BigDecimal("8.50");
            case IPCA -> new BigDecimal("4.25");
            case IGP_M -> new BigDecimal("4.80");
            case NENHUM -> BigDecimal.ZERO;
        };
    }

    private BigDecimal calcularVariacaoPorPeriodo(Indice indice, int prazoMeses) {
        double volatilidade = getVolatilidadeIndice(indice);
        double fatorTempo = calcularFatorTempo(prazoMeses);
        double fatorCicloEconomico = calcularCicloEconomico(prazoMeses);

        double variacao = volatilidade * fatorTempo * fatorCicloEconomico *
                (random.nextGaussian() * 0.5);

        return new BigDecimal(variacao).setScale(2, RoundingMode.HALF_UP);
    }

    private double getVolatilidadeIndice(Indice indice) {
        return switch (indice) {
            case SELIC -> 0.8;
            case CDI -> 0.7;
            case IPCA -> 1.2;
            case IGP_M -> 1.5;
            case IBOVESPA -> 3.0;
            case NENHUM -> 0.0;
        };
    }

    private double calcularFatorTempo(int prazoMeses) {
        if (prazoMeses <= 6) return 0.3;
        if (prazoMeses <= 12) return 0.6;
        if (prazoMeses <= 24) return 0.9;
        return 1.2;
    }

    private double calcularCicloEconomico(int prazoMeses) {

        if (prazoMeses <= 3) {
            return 1.0;
        } else if (prazoMeses <= 12) {

            return 0.8 + (random.nextDouble() * 0.4);
        } else if (prazoMeses <= 24) {

            return random.nextBoolean() ?
                    0.6 + (random.nextDouble() * 0.3) :
                    1.1 + (random.nextDouble() * 0.4);
        } else {

            return 0.5 + (random.nextDouble() * 1.0);
        }
    }

    public String getCenarioSimulado(Indice indice, int prazoMeses, BigDecimal taxaSimulada) {
        BigDecimal taxaBase = getTaxaBase(indice);
        BigDecimal diferenca = taxaSimulada.subtract(taxaBase);

        String tendencia;
        if (diferenca.compareTo(new BigDecimal("0.5")) > 0) {
            tendencia = "em alta";
        } else if (diferenca.compareTo(new BigDecimal("-0.5")) < 0) {
            tendencia = "em baixa";
        } else {
            tendencia = "estável";
        }

        String periodo = getPeriodoDescricao(prazoMeses);

        return String.format("Simulação para %s: %s %s no período de %s",
                indice.getDescricao(), indice.getDescricao(), tendencia, periodo);
    }

    private String getPeriodoDescricao(int prazoMeses) {
        if (prazoMeses <= 3) return "curto prazo";
        if (prazoMeses <= 6) return "médio-curto prazo";
        if (prazoMeses <= 12) return "médio prazo";
        if (prazoMeses <= 24) return "longo prazo";
        return "muito longo prazo";
    }

    public boolean isValorSimulado() {
        return true;
    }
}

