package org.example.model;

public enum TipoRentabilidade {
    PRE("Pré-fixado"),
    POS("Pós-fixado");

    private final String descricao;

    TipoRentabilidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}