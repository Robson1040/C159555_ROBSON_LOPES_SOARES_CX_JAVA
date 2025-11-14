package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.dto.ClienteRequest;
import org.example.dto.ClienteResponse;
import org.example.dto.ClienteUpdateRequest;
import org.example.exception.ClienteNotFoundException;
import org.example.model.Pessoa;

import java.util.List;

/**
 * Serviço para operações de CRUD de clientes
 */
@ApplicationScoped
public class ClienteService {

    @Inject
    PasswordService passwordService;

    /**
     * Lista todos os clientes
     */
    public List<ClienteResponse> listarTodos() {
        List<Pessoa> pessoas = Pessoa.listAll();
        return ClienteResponse.fromList(pessoas);
    }

    /**
     * Busca cliente por ID
     */
    public ClienteResponse buscarPorId(Long id) {
        Pessoa pessoa = Pessoa.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }
        return new ClienteResponse(pessoa);
    }

    /**
     * Cria um novo cliente
     */
    @Transactional
    public ClienteResponse criar(@Valid ClienteRequest request) {
        // Valida se CPF já existe
        if (Pessoa.existsByCpf(request.cpf())) {
            throw new IllegalArgumentException("Já existe um cliente com este CPF");
        }

        // Valida se username já existe
        if (Pessoa.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Já existe um cliente com este username");
        }

        // Criptografa a senha
        String senhaCriptografada = passwordService.encryptPassword(request.password());

        // Cria a pessoa
        Pessoa pessoa = new Pessoa(
                request.nome(),
                request.cpf(),
                request.username(),
                senhaCriptografada,
                request.role()
        );

        pessoa.persist();
        return new ClienteResponse(pessoa);
    }

    /**
     * Atualiza um cliente existente
     */
    @Transactional
    public ClienteResponse atualizar(Long id, @Valid ClienteUpdateRequest request) {
        Pessoa pessoa = Pessoa.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }

        // Atualiza apenas campos não nulos
        if (request.nome() != null && !request.nome().trim().isEmpty()) {
            pessoa.nome = request.nome();
        }

        if (request.username() != null && !request.username().trim().isEmpty()) {
            // Verifica se o novo username já existe em outro cliente
            Pessoa existente = Pessoa.findByUsername(request.username());
            if (existente != null && !existente.id.equals(id)) {
                throw new IllegalArgumentException("Já existe um cliente com este username");
            }
            pessoa.username = request.username();
        }

        if (request.password() != null && !request.password().trim().isEmpty()) {
            pessoa.password = passwordService.encryptPassword(request.password());
        }

        pessoa.persist();
        return new ClienteResponse(pessoa);
    }

    /**
     * Remove um cliente
     */
    @Transactional
    public void deletar(Long id) {
        Pessoa pessoa = Pessoa.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }

        pessoa.delete();
    }

    /**
     * Busca cliente por CPF
     */
    public ClienteResponse buscarPorCpf(String cpf) {
        Pessoa pessoa = Pessoa.findByCpf(cpf);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com CPF: " + cpf);
        }
        return new ClienteResponse(pessoa);
    }

    /**
     * Busca cliente por username
     */
    public ClienteResponse buscarPorUsername(String username) {
        Pessoa pessoa = Pessoa.findByUsername(username);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com username: " + username);
        }
        return new ClienteResponse(pessoa);
    }
}