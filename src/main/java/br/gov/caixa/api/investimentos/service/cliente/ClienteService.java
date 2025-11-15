package br.gov.caixa.api.investimentos.service.cliente;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteUpdateRequest;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.mapper.PessoaMapper;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import br.gov.caixa.api.investimentos.repository.cliente.IPessoaRepository;
import br.gov.caixa.api.investimentos.service.autenticacao.PasswordService;

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

    @Inject
    IPessoaRepository pessoaRepository;

    /**
     * Lista todos os clientes
     */
    public List<ClienteResponse> listarTodos() {
        List<Pessoa> pessoas = pessoaRepository.listAll();
        return pessoaMapper.toClienteResponseList(pessoas);
    }

    /**
     * Busca cliente por ID
     */
    public ClienteResponse buscarPorId(Long id) {
        Pessoa pessoa = pessoaRepository.findById(id);
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
        if (pessoaRepository.existsByCpf(request.cpf())) {
            throw new IllegalArgumentException("Já existe um cliente com este CPF");
        }

        // Valida se username já existe
        if (pessoaRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Já existe um cliente com este username");
        }

        // Cria a pessoa usando o mapper
        Pessoa pessoa = pessoaMapper.toEntity(request);
        
        // Criptografa a senha
        pessoa.setPassword(passwordService.encryptPassword(request.password()));

        pessoaRepository.persist(pessoa);
        return pessoaMapper.toClienteResponse(pessoa);
    }

    /**
     * Atualiza um cliente existente
     */
    @Transactional
    public ClienteResponse atualizar(Long id, @Valid ClienteUpdateRequest request) {
        Pessoa pessoa = pessoaRepository.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }

        // Validações específicas antes de usar o mapper
        if (request.username() != null && !request.username().trim().isEmpty()) {
            // Verifica se o novo username já existe em outro cliente
            Pessoa existente = pessoaRepository.findByUsername(request.username());
            if (existente != null && !existente.getId().equals(id)) {
                throw new IllegalArgumentException("Já existe um cliente com este username");
            }
        }

        // Usa o mapper para atualizar
        pessoaMapper.updateEntityFromRequest(pessoa, request);

        // Criptografa a senha se foi fornecida
        if (request.password() != null && !request.password().trim().isEmpty()) {
            pessoa.setPassword(passwordService.encryptPassword(request.password()));
        }

        pessoaRepository.persist(pessoa);
        return pessoaMapper.toClienteResponse(pessoa);
    }

    /**
     * Remove um cliente
     */
    @Transactional
    public void deletar(Long id) {
        Pessoa pessoa = pessoaRepository.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }

        pessoaRepository.delete(pessoa);
    }

    /**
     * Busca cliente por CPF
     */
    public ClienteResponse buscarPorCpf(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com CPF: " + cpf);
        }
        return pessoaMapper.toClienteResponse(pessoa);
    }

    /**
     * Busca cliente por username
     */
    public ClienteResponse buscarPorUsername(String username) {
        Pessoa pessoa = pessoaRepository.findByUsername(username);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com username: " + username);
        }
        return pessoaMapper.toClienteResponse(pessoa);
    }
}

