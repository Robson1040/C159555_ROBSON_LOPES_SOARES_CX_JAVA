package br.gov.caixa.api.investimentos.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteUpdateRequest;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;

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
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getCpf(),
                pessoa.getUsername(),
                pessoa.getRole()
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
                .filter(pessoa -> pessoa != null)
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
            pessoa.setNome(request.nome());
        }
        if (request.username() != null) {
            pessoa.setUsername(request.username());
        }
        if (request.password() != null) {
            pessoa.setPassword(request.password());
        }
    }
}
