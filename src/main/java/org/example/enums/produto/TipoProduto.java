package org.example.enums.produto;

import org.example.enums.simulacao.TipoRenda;

public enum TipoProduto {
    CDB("Certificado de Depósito Bancário", TipoRenda.RENDA_FIXA),
    LCI("Letra de Crédito Imobiliário", TipoRenda.RENDA_FIXA),
    LCA("Letra de Crédito do Agronegócio", TipoRenda.RENDA_FIXA),
    FUNDO("Fundo de Investimento", TipoRenda.RENDA_VARIAVEL), // Pode ser misto, mas vamos considerar variável como padrão
    TESOURO_DIRETO("Tesouro Direto", TipoRenda.RENDA_FIXA),
    POUPANCA("Poupança", TipoRenda.RENDA_FIXA);

    private final String descricao;
    private final TipoRenda tipoRenda;

    TipoProduto(String descricao, TipoRenda tipoRenda) {
        this.descricao = descricao;
        this.tipoRenda = tipoRenda;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRenda getTipoRenda() {
        return tipoRenda;
    }
}
