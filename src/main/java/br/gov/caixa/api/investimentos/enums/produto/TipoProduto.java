package br.gov.caixa.api.investimentos.enums.produto;

import br.gov.caixa.api.investimentos.enums.simulacao.TipoRenda;

public enum TipoProduto {
    CDB("Certificado de Depósito Bancário", TipoRenda.RENDA_FIXA, 1),
    LCI("Letra de Crédito Imobiliário", TipoRenda.RENDA_FIXA, 2),
    LCA("Letra de Crédito do Agronegócio", TipoRenda.RENDA_FIXA, 3),
    TESOURO_DIRETO("Tesouro Direto", TipoRenda.RENDA_FIXA, 4),
    POUPANCA("Poupança", TipoRenda.RENDA_FIXA, 5),
    DEBENTURE("Debênture", TipoRenda.RENDA_FIXA, 6),
    CRI("Certificado de Recebíveis Imobiliários", TipoRenda.RENDA_FIXA, 7),
    FUNDO("Fundo de Investimento", TipoRenda.RENDA_VARIAVEL, 8),
    FII("Fundo de Investimento Imobiliário", TipoRenda.RENDA_VARIAVEL, 9),
    ACAO("Ação", TipoRenda.RENDA_VARIAVEL, 10),
    ETF("ETF", TipoRenda.RENDA_VARIAVEL, 11);

    private final String descricao;
    private final TipoRenda tipoRenda;
    private final int valor;

    TipoProduto(String descricao, TipoRenda tipoRenda, int valor) {
        this.descricao = descricao;
        this.tipoRenda = tipoRenda;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRenda getTipoRenda() {
        return tipoRenda;
    }

    public int getValor() {
        return valor;
    }
}