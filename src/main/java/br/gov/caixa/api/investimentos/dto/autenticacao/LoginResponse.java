package br.gov.caixa.api.investimentos.dto.autenticacao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record LoginResponse(
        @JsonProperty("token")
        String token,

        @JsonProperty("tipo")
        String tipo,

        @JsonProperty("expira_em")
        LocalDateTime expiraEm,

        @JsonProperty("usuario")
        String usuario,

        @JsonProperty("role")
        String role
) {
}