/*
package br.gov.caixa.api.investimentos.service.compliance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço para controle de limites regulatórios
 * Implementa regras FGC, BACEN e CVM
 */
/*
@ApplicationScoped
public class LimitesRegulatóriosService {

    // Limites FGC por CPF por instituição
    private static final BigDecimal LIMITE_FGC_GERAL = new BigDecimal("250000.00");
    private static final BigDecimal LIMITE_FGC_POUPANCA = new BigDecimal("250000.00");
    private static final BigDecimal LIMITE_FGC_LCI_LCA = new BigDecimal("250000.00");

    @Inject
    IInvestimentoRepository investimentoRepository;

    /**
     * Verifica se o novo investimento respeita os limites FGC

    public boolean validarLimiteFGC(String cpf, TipoProduto tipoProduto, BigDecimal valorNovo) {
        if (!temProtecaoFGC(tipoProduto)) {
            return true; // Sem limite se não tem FGC
        }

        BigDecimal totalAtual = calcularTotalFGCPorCpf(cpf, tipoProduto);
        BigDecimal novoTotal = totalAtual.add(valorNovo);

        return novoTotal.compareTo(getLimiteFGC(tipoProduto)) <= 0;
    }

    /**
     * Calcula total investido com proteção FGC por CPF

    private BigDecimal calcularTotalFGCPorCpf(String cpf, TipoProduto tipoProduto) {
        // Implementar consulta ao repositório
        // Somar todos os investimentos do CPF com FGC do mesmo tipo
        return BigDecimal.ZERO; // Placeholder
    }

    /**
     * Verifica se produto tem proteção FGC

    private boolean temProtecaoFGC(TipoProduto tipoProduto) {
        return switch (tipoProduto) {
            case CDB, LCI, LCA, POUPANCA -> true;
            case TESOURO_DIRETO, FUNDO, FII, DEBENTURE, CRI -> false;
        };
    }

    /**
     * Retorna limite FGC específico por tipo de produto

    private BigDecimal getLimiteFGC(TipoProduto tipoProduto) {
        return switch (tipoProduto) {
            case POUPANCA -> LIMITE_FGC_POUPANCA;
            case LCI, LCA -> LIMITE_FGC_LCI_LCA;
            default -> LIMITE_FGC_GERAL;
        };
    }

    /**
     * Calcula valor disponível para investimento respeitando FGC

    public BigDecimal calcularValorDisponivelFGC(String cpf, TipoProduto tipoProduto) {
        if (!temProtecaoFGC(tipoProduto)) {
            return new BigDecimal("999999999.99"); // Sem limite
        }

        BigDecimal totalAtual = calcularTotalFGCPorCpf(cpf, tipoProduto);
        BigDecimal limite = getLimiteFGC(tipoProduto);
        
        BigDecimal disponivel = limite.subtract(totalAtual);
        return disponivel.max(BigDecimal.ZERO);
    }
}
*/