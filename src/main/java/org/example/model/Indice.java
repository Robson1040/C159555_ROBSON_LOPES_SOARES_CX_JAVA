package org.example.model;

public enum Indice {
    SELIC("Selic"),
    CDI("CDI"),
    IBOVESPA("Ibovespa"),
    IPCA("IPCA"),
    IGP_M("IGP-M"),
    NENHUM("Nenhum"); // Para produtos pré-fixados

    private final String descricao;

    Indice(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}