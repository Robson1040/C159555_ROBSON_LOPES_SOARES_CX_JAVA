package br.gov.caixa.api.investimentos.dto.cliente;

import br.gov.caixa.api.investimentos.validation.ValidCPF;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @JsonProperty("nome")
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nome,

        @JsonProperty("cpf")
        @NotBlank(message = "CPF é obrigatório")
        @ValidCPF
        String cpf,

        @JsonProperty("username")
        @NotBlank(message = "Username é obrigatório")
        @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
        String username,

        @JsonProperty("password")
        @NotBlank(message = "Password é obrigatório")
        @Size(min = 6, message = "Password deve ter no mínimo 6 caracteres")
        String password,

        @JsonProperty("role")
        @NotBlank(message = "Role é obrigatório")
        @Pattern(regexp = "USER|ADMIN", message = "Role deve ser 'USER' ou 'ADMIN'")
        String role
) {
}