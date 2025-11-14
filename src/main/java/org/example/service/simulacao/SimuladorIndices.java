package org.example.service.simulacao;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.enums.Indice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Simulador de índices econômicos que varia os valores baseado no período
 * e adiciona volatilidade realista aos cálculos
 */
@ApplicationScoped
public class SimuladorIndices {

    private final Random random = new Random();

    /**
     * Obtém a taxa simulada do índice considerando o período do investimento
     * 
     * @param indice Índice a ser simulado
     * @param prazoMeses Prazo do investimento em meses
     * @return Taxa anual simulada com variação baseada no período
     */
    public BigDecimal getTaxaSimulada(Indice indice, int prazoMeses) {
        BigDecimal taxaBase = getTaxaBase(indice);
        BigDecimal variacao = calcularVariacaoPorPeriodo(indice, prazoMeses);
        
        return taxaBase.add(variacao).max(BigDecimal.ZERO);
    }

    /**
     * Define as taxas base para cada índice (valores médios anuais)
     */
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

    /**
     * Calcula variação baseada no período e volatilidade do índice
     */
    private BigDecimal calcularVariacaoPorPeriodo(Indice indice, int prazoMeses) {
        double volatilidade = getVolatilidadeIndice(indice);
        double fatorTempo = calcularFatorTempo(prazoMeses);
        double fatorCicloEconomico = calcularCicloEconomico(prazoMeses);
        
        // Variação = volatilidade * fator_tempo * fator_ciclo * aleatoriedade
        double variacao = volatilidade * fatorTempo * fatorCicloEconomico * 
                         (random.nextGaussian() * 0.5); // Distribuição normal
        
        return new BigDecimal(variacao).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Define a volatilidade histórica de cada índice
     */
    private double getVolatilidadeIndice(Indice indice) {
        return switch (indice) {
            case SELIC -> 0.8;   // Baixa volatilidade
            case CDI -> 0.7;     // Baixa volatilidade
            case IPCA -> 1.2;    // Volatilidade moderada
            case IGP_M -> 1.5;   // Volatilidade moderada-alta
            case IBOVESPA -> 3.0; // Alta volatilidade
            case NENHUM -> 0.0;
        };
    }

    /**
     * Calcula fator baseado no tempo - períodos mais longos têm mais variação
     */
    private double calcularFatorTempo(int prazoMeses) {
        if (prazoMeses <= 6) return 0.3;      // Curto prazo - menor variação
        if (prazoMeses <= 12) return 0.6;     // Médio prazo
        if (prazoMeses <= 24) return 0.9;     // Longo prazo
        return 1.2;                           // Muito longo prazo - maior variação
    }

    /**
     * Simula ciclos econômicos baseado no período
     */
    private double calcularCicloEconomico(int prazoMeses) {
        // Simula tendências econômicas baseadas no período
        if (prazoMeses <= 3) {
            return 1.0; // Curto prazo - sem ajuste de ciclo
        } else if (prazoMeses <= 12) {
            // Médio prazo - pequena tendência
            return 0.8 + (random.nextDouble() * 0.4); // Entre 0.8 e 1.2
        } else if (prazoMeses <= 24) {
            // Longo prazo - possível recessão ou crescimento
            return random.nextBoolean() ? 
                   0.6 + (random.nextDouble() * 0.3) :  // Cenário pessimista (0.6-0.9)
                   1.1 + (random.nextDouble() * 0.4);   // Cenário otimista (1.1-1.5)
        } else {
            // Muito longo prazo - ciclos completos
            return 0.5 + (random.nextDouble() * 1.0); // Entre 0.5 e 1.5
        }
    }

    /**
     * Obtém cenário descritivo para o índice no período
     */
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

    /**
     * Verifica se o valor é simulado (sempre true para esta implementação)
     */
    public boolean isValorSimulado() {
        return true;
    }
}