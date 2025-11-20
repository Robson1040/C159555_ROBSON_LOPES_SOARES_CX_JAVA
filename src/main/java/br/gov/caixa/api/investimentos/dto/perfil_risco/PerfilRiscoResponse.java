package br.gov.caixa.api.investimentos.dto.perfil_risco;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PerfilRiscoResponse(
        @JsonProperty("clienteId")
        Long clienteId,

        @JsonProperty("perfil")
        String perfil,

        @JsonProperty("pontuacao")
        Integer pontuacao,

        @JsonProperty("descricao")
        String descricao
) {

    public static PerfilRiscoResponse conservador(Long clienteId, Integer pontuacao) {
        return new PerfilRiscoResponse(
                clienteId,
                "CONSERVADOR",
                pontuacao,
                "Perfil focado em segurança e liquidez, com baixa tolerância ao risco."
        );
    }

    public static PerfilRiscoResponse moderado(Long clienteId, Integer pontuacao) {
        return new PerfilRiscoResponse(
                clienteId,
                "MODERADO",
                pontuacao,
                "Perfil equilibrado entre segurança e rentabilidade."
        );
    }

    public static PerfilRiscoResponse agressivo(Long clienteId, Integer pontuacao) {
        return new PerfilRiscoResponse(
                clienteId,
                "AGRESSIVO",
                pontuacao,
                "Perfil voltado para alta rentabilidade, com maior tolerância ao risco."
        );
    }
}