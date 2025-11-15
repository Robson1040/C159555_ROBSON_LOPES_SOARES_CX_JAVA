package br.gov.caixa.api.investimentos.dto.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para resposta de cliente
 */
public record ClienteResponse(
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("nome")
        String nome,
        
        @JsonProperty("cpf")
        String cpf,
        
        @JsonProperty("username")
        String username,
        
        @JsonProperty("role")
        String role
) {}
