package br.gov.caixa.api.investimentos.dto.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;


public record ClienteUpdateRequest(
        @JsonProperty("nome")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nome,
        
        @JsonProperty("username")
        @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
        String username,
        
        @JsonProperty("password")
        @Size(min = 6, message = "Password deve ter no mínimo 6 caracteres")
        String password
) {}