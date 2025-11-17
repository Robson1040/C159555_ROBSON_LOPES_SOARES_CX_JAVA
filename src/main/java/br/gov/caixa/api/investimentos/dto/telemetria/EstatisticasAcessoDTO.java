package br.gov.caixa.api.investimentos.dto.telemetria;

/**
 * DTO para estatísticas de acesso
 */
public record EstatisticasAcessoDTO(
    long totalAcessos,
    long acessosComSucesso,
    long acessosComErro,
    double taxaSucesso,
    double taxaErro
) {}
