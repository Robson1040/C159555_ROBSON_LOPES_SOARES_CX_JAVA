package br.gov.caixa.api.investimentos.client;

import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Random;

/* SIMULA INDICES DE MERCADO
 * COMO SE FOSSE UMA API EXTERNA */
@ApplicationScoped
public class SimuladorMercado {

    private final Random random = new Random();

    public CenarioMercado gerarCenario(TipoProduto tipoProduto, int prazoMeses) {
        CenarioEconomico cenario = definirCenarioEconomico(prazoMeses);
        BigDecimal multiplicadorRisco = calcularMultiplicadorRisco(tipoProduto, cenario);
        String descricao = gerarDescricaoCenario(cenario, prazoMeses);

        return new CenarioMercado(cenario, multiplicadorRisco, descricao, true);
    }

    private CenarioEconomico definirCenarioEconomico(int prazoMeses) {

        double probabilidade = random.nextDouble();

        if (prazoMeses <= 6) {

            if (probabilidade < 0.6) return CenarioEconomico.ESTAVEL;
            if (probabilidade < 0.8) return CenarioEconomico.CRESCIMENTO_MODERADO;
            return CenarioEconomico.VOLATIL;
        } else if (prazoMeses <= 12) {

            if (probabilidade < 0.3) return CenarioEconomico.RECESSAO_LEVE;
            if (probabilidade < 0.5) return CenarioEconomico.ESTAVEL;
            if (probabilidade < 0.75) return CenarioEconomico.CRESCIMENTO_MODERADO;
            return CenarioEconomico.CRESCIMENTO_FORTE;
        } else if (prazoMeses <= 24) {

            if (probabilidade < 0.2) return CenarioEconomico.RECESSAO_FORTE;
            if (probabilidade < 0.35) return CenarioEconomico.RECESSAO_LEVE;
            if (probabilidade < 0.5) return CenarioEconomico.ESTAVEL;
            if (probabilidade < 0.7) return CenarioEconomico.CRESCIMENTO_MODERADO;
            if (probabilidade < 0.9) return CenarioEconomico.CRESCIMENTO_FORTE;
            return CenarioEconomico.BOOM_ECONOMICO;
        } else {

            if (probabilidade < 0.15) return CenarioEconomico.RECESSAO_FORTE;
            if (probabilidade < 0.25) return CenarioEconomico.RECESSAO_LEVE;
            if (probabilidade < 0.4) return CenarioEconomico.ESTAVEL;
            if (probabilidade < 0.6) return CenarioEconomico.CRESCIMENTO_MODERADO;
            if (probabilidade < 0.8) return CenarioEconomico.CRESCIMENTO_FORTE;
            if (probabilidade < 0.95) return CenarioEconomico.BOOM_ECONOMICO;
            return CenarioEconomico.VOLATIL;
        }
    }

    private BigDecimal calcularMultiplicadorRisco(TipoProduto tipoProduto, CenarioEconomico cenario) {
        BigDecimal baseMultiplier = getMultiplicadorBaseProduto(tipoProduto);
        BigDecimal cenarioAdjustment = getAjusteCenario(cenario);

        return baseMultiplier.multiply(cenarioAdjustment)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal getMultiplicadorBaseProduto(TipoProduto tipoProduto) {
        return switch (tipoProduto) {
            case POUPANCA -> new BigDecimal("0.95");
            case CDB, LCI, LCA -> new BigDecimal("1.00");
            case TESOURO_DIRETO -> new BigDecimal("1.02");
            case DEBENTURE -> new BigDecimal("-0.06");
            case CRI -> new BigDecimal("-0.06");
            case FUNDO -> new BigDecimal("1.15");
            case FII -> new BigDecimal("-0.06");
            case ACAO -> new BigDecimal("-0.06");
            case ETF -> new BigDecimal("-0.06");
        };
    }

    private BigDecimal getAjusteCenario(CenarioEconomico cenario) {
        return switch (cenario) {
            case RECESSAO_FORTE -> new BigDecimal("0.70");
            case RECESSAO_LEVE -> new BigDecimal("0.85");
            case ESTAVEL -> new BigDecimal("1.00");
            case CRESCIMENTO_MODERADO -> new BigDecimal("1.15");
            case CRESCIMENTO_FORTE -> new BigDecimal("1.30");
            case BOOM_ECONOMICO -> new BigDecimal("1.50");
            case VOLATIL -> new BigDecimal("0.80");
        };
    }

    private String gerarDescricaoCenario(CenarioEconomico cenario, int prazoMeses) {
        String periodo = getPeriodoDescricao(prazoMeses);
        String descricaoCenario = getDescricaoCenario(cenario);
        LocalDate dataSimulacao = LocalDate.now();

        return String.format("Simulação para %s (%s) - Cenário: %s - Data base: %s",
                periodo, prazoMeses + " meses", descricaoCenario, dataSimulacao);
    }

    private String getPeriodoDescricao(int prazoMeses) {
        if (prazoMeses <= 3) return "Curto Prazo";
        if (prazoMeses <= 6) return "Médio-Curto Prazo";
        if (prazoMeses <= 12) return "Médio Prazo";
        if (prazoMeses <= 24) return "Longo Prazo";
        return "Muito Longo Prazo";
    }

    private String getDescricaoCenario(CenarioEconomico cenario) {
        return switch (cenario) {
            case RECESSAO_FORTE -> "Recessão Forte - Queda significativa na atividade econômica";
            case RECESSAO_LEVE -> "Recessão Leve - Desaceleração moderada da economia";
            case ESTAVEL -> "Economia Estável - Crescimento moderado e inflação controlada";
            case CRESCIMENTO_MODERADO -> "Crescimento Moderado - Expansão econômica sustentável";
            case CRESCIMENTO_FORTE -> "Crescimento Forte - Expansão acelerada da economia";
            case BOOM_ECONOMICO -> "Boom Econômico - Crescimento excepcional com possível superaquecimento";
            case VOLATIL -> "Alta Volatilidade - Incertezas e oscilações frequentes no mercado";
        };
    }

    public BigDecimal ajustarRentabilidadePorCenario(BigDecimal rentabilidadeBase,
                                                     CenarioMercado cenario,
                                                     TipoRentabilidade tipoRentabilidade) {
        BigDecimal multiplicador = cenario.getMultiplicadorRisco();

        if (TipoRentabilidade.POS.equals(tipoRentabilidade)) {
            multiplicador = multiplicador.multiply(new BigDecimal("1.2"));
        }

        return rentabilidadeBase.multiply(multiplicador)
                .setScale(4, RoundingMode.HALF_UP);
    }

    public enum CenarioEconomico {
        RECESSAO_FORTE,
        RECESSAO_LEVE,
        ESTAVEL,
        CRESCIMENTO_MODERADO,
        CRESCIMENTO_FORTE,
        BOOM_ECONOMICO,
        VOLATIL
    }

    public static class CenarioMercado {
        private final CenarioEconomico cenario;
        private final BigDecimal multiplicadorRisco;
        private final String descricao;
        private final boolean simulado;

        public CenarioMercado(CenarioEconomico cenario, BigDecimal multiplicadorRisco,
                              String descricao, boolean simulado) {
            this.cenario = cenario;
            this.multiplicadorRisco = multiplicadorRisco;
            this.descricao = descricao;
            this.simulado = simulado;
        }

        public CenarioEconomico getCenario() {
            return cenario;
        }

        public BigDecimal getMultiplicadorRisco() {
            return multiplicadorRisco;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isSimulado() {
            return simulado;
        }
    }
}

