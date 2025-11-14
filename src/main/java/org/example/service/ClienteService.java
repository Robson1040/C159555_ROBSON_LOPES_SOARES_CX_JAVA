package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.dto.ClienteRequest;
import org.example.dto.ClienteResponse;
import org.example.dto.ClienteUpdateRequest;
import org.example.exception.ClienteNotFoundException;
import org.example.mapper.PessoaMapper;
import org.example.model.Pessoa;

import java.util.List;

/**
 * Serviço para operações de CRUD de clientes
 */
@ApplicationScoped
public class ClienteService {

    @Inject
    PasswordService passwordService;

    @Inject
    PessoaMapper pessoaMapper;

    /**
     * Lista todos os clientes
     */
    public List<ClienteResponse> listarTodos() {
        List<Pessoa> pessoas = Pessoa.listAll();
        return pessoaMapper.toClienteResponseList(pessoas);
    }

    /**
     * Busca cliente por ID
     */
    public ClienteResponse buscarPorId(Long id) {
        Pessoa pessoa = Pessoa.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }
        return pessoaMapper.toClienteResponse(pessoa);
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

        // Cria a pessoa usando o mapper
        Pessoa pessoa = pessoaMapper.toEntity(request);
        
        // Criptografa a senha
        pessoa.password = passwordService.encryptPassword(request.password());

        pessoa.persist();
        return pessoaMapper.toClienteResponse(pessoa);
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

        // Validações específicas antes de usar o mapper
        if (request.username() != null && !request.username().trim().isEmpty()) {
            // Verifica se o novo username já existe em outro cliente
            Pessoa existente = Pessoa.findByUsername(request.username());
            if (existente != null && !existente.id.equals(id)) {
                throw new IllegalArgumentException("Já existe um cliente com este username");
            }
        }

        // Usa o mapper para atualizar
        pessoaMapper.updateEntityFromRequest(pessoa, request);

        // Criptografa a senha se foi fornecida
        if (request.password() != null && !request.password().trim().isEmpty()) {
            pessoa.password = passwordService.encryptPassword(request.password());
        }

        pessoa.persist();
        return pessoaMapper.toClienteResponse(pessoa);
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
        return pessoaMapper.toClienteResponse(pessoa);
    }

    /**
     * Busca cliente por username
     */
    public ClienteResponse buscarPorUsername(String username) {
        Pessoa pessoa = Pessoa.findByUsername(username);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com username: " + username);
        }
        return pessoaMapper.toClienteResponse(pessoa);
    }
}