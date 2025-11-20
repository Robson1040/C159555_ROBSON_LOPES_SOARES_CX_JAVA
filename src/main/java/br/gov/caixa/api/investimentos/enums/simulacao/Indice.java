package br.gov.caixa.api.investimentos.enums.simulacao;

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

    public BigDecimal getTaxaAnualSimulada(int prazoMeses) {
        if (this == NENHUM) {
            return BigDecimal.ZERO;
        }

        return getTaxaFixaFallback();
    }

    public BigDecimal getTaxaDecimal(int prazoMeses) {
        return getTaxaAnualSimulada(prazoMeses).divide(new BigDecimal("100"));
    }

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

    @Deprecated
    public BigDecimal getTaxaAnualSimulada() {
        return getTaxaAnualSimulada(12);
    }

    @Deprecated
    public BigDecimal getTaxaDecimal() {
        return getTaxaDecimal(12);
    }
}
