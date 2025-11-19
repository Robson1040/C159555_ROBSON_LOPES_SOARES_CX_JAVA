package br.gov.caixa.api.investimentos.service.compliance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;

import java.math.BigDecimal;


@ApplicationScoped
public class LimitesRegulatóriosService {

    
    private static final BigDecimal LIMITE_FGC_GERAL = new BigDecimal("250000.00");
    private static final BigDecimal LIMITE_FGC_POUPANCA = new BigDecimal("250000.00");
    private static final BigDecimal LIMITE_FGC_LCI_LCA = new BigDecimal("250000.00");

    @Inject
    IInvestimentoRepository investimentoRepository;

    
    public boolean validarLimiteFGC(String cpf, TipoProduto tipoProduto, BigDecimal valorNovo) {
        if (!temProtecaoFGC(tipoProduto)) {
            return true; 
        }

        BigDecimal totalAtual = calcularTotalFGCPorCpf(cpf, tipoProduto);
        BigDecimal novoTotal = totalAtual.add(valorNovo);

        return novoTotal.compareTo(getLimiteFGC(tipoProduto)) <= 0;
    }

    
    private BigDecimal calcularTotalFGCPorCpf(String cpf, TipoProduto tipoProduto) {
        
        
        return BigDecimal.ZERO; 
    }

    
    private boolean temProtecaoFGC(TipoProduto tipoProduto) {
        return switch (tipoProduto) {
            case CDB, LCI, LCA, POUPANCA -> true;
            case TESOURO_DIRETO, FUNDO, FII, DEBENTURE, CRI, ACAO, ETF -> false;
        };
    }

    
    private BigDecimal getLimiteFGC(TipoProduto tipoProduto) {
        return switch (tipoProduto) {
            case POUPANCA -> LIMITE_FGC_POUPANCA;
            case LCI, LCA -> LIMITE_FGC_LCI_LCA;
            default -> LIMITE_FGC_GERAL;
        };
    }

    
    public BigDecimal calcularValorDisponivelFGC(String cpf, TipoProduto tipoProduto) {
        if (!temProtecaoFGC(tipoProduto)) {
            return new BigDecimal("999999999.99"); 
        }

        BigDecimal totalAtual = calcularTotalFGCPorCpf(cpf, tipoProduto);
        BigDecimal limite = getLimiteFGC(tipoProduto);
        
        BigDecimal disponivel = limite.subtract(totalAtual);
        return disponivel.max(BigDecimal.ZERO);
    }
}