package br.gov.caixa.api.investimentos.service.compliance;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serviço para cálculos de tributação em investimentos
 * Implementa as regras do IR e IOF conforme legislação brasileira
 */
@ApplicationScoped
public class TributacaoService {

    /**
     * Calcula Imposto de Renda sobre rendimentos de investimentos
     * Tabela regressiva conforme Lei 11.033/2004
     */
    public BigDecimal calcularIR(BigDecimal rendimento, int prazoDias) {
        if (rendimento.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal aliquota;
        if (prazoDias <= 180) {
            aliquota = new BigDecimal("0.225"); // 22,5%
        } else if (prazoDias <= 360) {
            aliquota = new BigDecimal("0.20");  // 20%
        } else if (prazoDias <= 720) {
            aliquota = new BigDecimal("0.175"); // 17,5%
        } else {
            aliquota = new BigDecimal("0.15");  // 15%
        }

        return rendimento.multiply(aliquota).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula IOF sobre rendimentos até 30 dias
     * Tabela regressiva IOF conforme Decreto 6.306/2007
     */
    public BigDecimal calcularIOF(BigDecimal rendimento, int prazoDias) {
        if (prazoDias >= 30 || rendimento.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // IOF regressivo: 96% (1 dia) até 0% (30 dias)
        BigDecimal percentualIOF = new BigDecimal("96")
            .subtract(new BigDecimal(prazoDias - 1).multiply(new BigDecimal("3.33")))
            .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        if (percentualIOF.compareTo(BigDecimal.ZERO) < 0) {
            percentualIOF = BigDecimal.ZERO;
        }

        return rendimento.multiply(percentualIOF).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula rendimento líquido após IR e IOF
     */
    public BigDecimal calcularRendimentoLiquido(BigDecimal rendimentoBruto, int prazoDias) {
        BigDecimal ir = calcularIR(rendimentoBruto, prazoDias);
        BigDecimal iof = calcularIOF(rendimentoBruto, prazoDias);
        
        return rendimentoBruto.subtract(ir).subtract(iof);
    }
}