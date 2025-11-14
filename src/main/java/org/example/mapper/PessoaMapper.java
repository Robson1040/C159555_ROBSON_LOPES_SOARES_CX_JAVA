package org.example.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.dto.ClienteRequest;
import org.example.dto.ClienteResponse;
import org.example.dto.ClienteUpdateRequest;
import org.example.model.Pessoa;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PessoaMapper {

    /**
     * Converte uma entidade Pessoa para ClienteResponse
     */
    public ClienteResponse toClienteResponse(Pessoa pessoa) {
        if (pessoa == null) {
            return null;
        }

        return new ClienteResponse(
                pessoa.id,
                pessoa.nome,
                pessoa.cpf,
                pessoa.username,
                pessoa.role
        );
    }

    /**
     * Converte uma lista de entidades Pessoa para uma lista de ClienteResponse
     */
    public List<ClienteResponse> toClienteResponseList(List<Pessoa> pessoas) {
        if (pessoas == null) {
            return null;
        }

        return pessoas.stream()
                .map(this::toClienteResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converte um ClienteRequest para entidade Pessoa
     */
    public Pessoa toEntity(ClienteRequest request) {
        if (request == null) {
            return null;
        }

        return new Pessoa(
                request.nome(),
                request.cpf(),
                request.username(),
                request.password(),
                request.role()
        );
    }

    /**
     * Atualiza uma entidade Pessoa existente com dados de um ClienteUpdateRequest
     */
    public void updateEntityFromRequest(Pessoa pessoa, ClienteUpdateRequest request) {
        if (pessoa == null || request == null) {
            return;
        }

        if (request.nome() != null) {
            pessoa.nome = request.nome();
        }
        if (request.username() != null) {
            pessoa.username = request.username();
        }
        if (request.password() != null) {
            pessoa.password = request.password();
        }
    }
}