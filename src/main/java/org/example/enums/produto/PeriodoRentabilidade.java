package org.example.enums.produto;

public enum PeriodoRentabilidade {
    AO_DIA("Ao Dia"),
    AO_MES("Ao Mês"),
    AO_ANO("Ao Ano"),
    PERIODO_TOTAL("Período Total");

    private final String descricao;

    PeriodoRentabilidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
