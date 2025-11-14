package org.example.dto.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.model.Pessoa;

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
) {
    /**
     * Construtor a partir da entidade Pessoa
     */
    public ClienteResponse(Pessoa pessoa) {
        this(pessoa.id, pessoa.nome, pessoa.cpf, pessoa.username, pessoa.role);
    }
    
    /**
     * Método estático para converter lista de Pessoa para lista de ClienteResponse
     */
    public static java.util.List<ClienteResponse> fromList(java.util.List<Pessoa> pessoas) {
        return pessoas.stream()
                .map(ClienteResponse::new)
                .collect(java.util.stream.Collectors.toList());
    }
}