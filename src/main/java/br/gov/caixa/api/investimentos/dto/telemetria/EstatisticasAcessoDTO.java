package br.gov.caixa.api.investimentos.dto.telemetria;

public record EstatisticasAcessoDTO(
        long totalAcessos,
        long acessosComSucesso,
        long acessosComErro,
        double taxaSucesso,
        double taxaErro
) {
}
