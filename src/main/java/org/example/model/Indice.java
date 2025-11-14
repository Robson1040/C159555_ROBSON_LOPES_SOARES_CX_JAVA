package org.example.model;

import java.math.BigDecimal;

public enum Indice {
    SELIC("Selic"),
    CDI("CDI"),
    IBOVESPA("Ibovespa"),
    IPCA("IPCA"),
    IGP_M("IGP-M"),
    NENHUM("Nenhum");

    private final String descricao;

    Indice(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna a taxa anual simulada do índice baseada no período
     * Para uso dinâmico, deve ser chamado através do SimuladorIndices
     * @param prazoMeses período do investimento em meses  
     * @return taxa anual simulada em percentual (ex: 10.75 = 10,75% ao ano)
     */
    public BigDecimal getTaxaAnualSimulada(int prazoMeses) {
        if (this == NENHUM) {
            return BigDecimal.ZERO;
        }
        
        // Retorna valores base - a simulação dinâmica será feita pelo SimuladorIndices
        return getTaxaFixaFallback();
    }

    /**
     * Retorna a taxa anual simulada como decimal para cálculos
     * @param prazoMeses período do investimento em meses
     * @return taxa como decimal (ex: 0.1075 para 10,75%)
     */
    public BigDecimal getTaxaDecimal(int prazoMeses) {
        return getTaxaAnualSimulada(prazoMeses).divide(new BigDecimal("100"));
    }

    /**
     * Valores fixos de fallback caso o simulador não esteja disponível
     */
    private BigDecimal getTaxaFixaFallback() {
        return switch (this) {
            case SELIC -> new BigDecimal("10.75");
            case CDI -> new BigDecimal("10.65");
            case IBOVESPA -> new BigDecimal("8.50");
            case IPCA -> new BigDecimal("4.25");
            case IGP_M -> new BigDecimal("4.80");
            case NENHUM -> BigDecimal.ZERO;
        };
    }

    /**
     * Método de compatibilidade - usa período padrão de 12 meses
     * @deprecated Use getTaxaAnualSimulada(int prazoMeses) para valores dinâmicos
     */
    @Deprecated
    public BigDecimal getTaxaAnualSimulada() {
        return getTaxaAnualSimulada(12);
    }

    /**
     * Método de compatibilidade - usa período padrão de 12 meses  
     * @deprecated Use getTaxaDecimal(int prazoMeses) para valores dinâmicos
     */
    @Deprecated
    public BigDecimal getTaxaDecimal() {
        return getTaxaDecimal(12);
    }
}