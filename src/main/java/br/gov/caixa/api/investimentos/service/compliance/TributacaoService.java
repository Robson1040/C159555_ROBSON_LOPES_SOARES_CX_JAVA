package br.gov.caixa.api.investimentos.service.compliance;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class TributacaoService {

    public BigDecimal calcularIR(BigDecimal rendimento, int prazoDias) {
        if (rendimento.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal aliquota;
        if (prazoDias <= 180) {
            aliquota = new BigDecimal("0.225");
        } else if (prazoDias <= 360) {
            aliquota = new BigDecimal("0.20");
        } else if (prazoDias <= 720) {
            aliquota = new BigDecimal("0.175");
        } else {
            aliquota = new BigDecimal("0.15");
        }

        return rendimento.multiply(aliquota).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularIOF(BigDecimal rendimento, int prazoDias) {
        if (prazoDias >= 30 || rendimento.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentualIOF = new BigDecimal("96")
                .subtract(new BigDecimal(prazoDias - 1).multiply(new BigDecimal("3.33")))
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        if (percentualIOF.compareTo(BigDecimal.ZERO) < 0) {
            percentualIOF = BigDecimal.ZERO;
        }

        return rendimento.multiply(percentualIOF).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularRendimentoLiquido(BigDecimal rendimentoBruto, int prazoDias) {
        BigDecimal ir = calcularIR(rendimentoBruto, prazoDias);
        BigDecimal iof = calcularIOF(rendimentoBruto, prazoDias);

        return rendimentoBruto.subtract(ir).subtract(iof);
    }
}