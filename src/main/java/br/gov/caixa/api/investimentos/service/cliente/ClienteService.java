package br.gov.caixa.api.investimentos.service.cliente;

import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteUpdateRequest;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.mapper.PessoaMapper;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import br.gov.caixa.api.investimentos.repository.cliente.IPessoaRepository;
import br.gov.caixa.api.investimentos.service.autenticacao.PasswordService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class ClienteService {

    @Inject
    PasswordService passwordService;

    @Inject
    PessoaMapper pessoaMapper;

    @Inject
    IPessoaRepository pessoaRepository;

    public List<ClienteResponse> listarTodos() {
        List<Pessoa> pessoas = pessoaRepository.listAll();
        return pessoaMapper.toClienteResponseList(pessoas);
    }

    public ClienteResponse buscarPorId(Long id) {
        Pessoa pessoa = pessoaRepository.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }
        return pessoaMapper.toClienteResponse(pessoa);
    }

    @Transactional
    public ClienteResponse criar(@Valid ClienteRequest request) {

        if (pessoaRepository.existsByCpf(request.cpf())) {
            throw new IllegalArgumentException("Já existe um cliente com este CPF");
        }

        if (pessoaRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Já existe um cliente com este username");
        }

        Pessoa pessoa = pessoaMapper.toEntity(request);

        pessoa.setPassword(passwordService.encryptPassword(request.password()));

        pessoaRepository.persist(pessoa);

        return pessoaMapper.toClienteResponse(pessoa);
    }

    @Transactional
    public ClienteResponse atualizar(Long id, @Valid ClienteUpdateRequest request) {
        Pessoa pessoa = pessoaRepository.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }

        if (request.username() != null && !request.username().trim().isEmpty()) {

            Pessoa existente = pessoaRepository.findByUsername(request.username());
            if (existente != null && !existente.getId().equals(id)) {
                throw new IllegalArgumentException("Já existe um cliente com este username");
            }
        }

        pessoaMapper.updateEntityFromRequest(pessoa, request);

        if (request.password() != null && !request.password().trim().isEmpty()) {
            pessoa.setPassword(passwordService.encryptPassword(request.password()));
        }

        pessoaRepository.persist(pessoa);
        return pessoaMapper.toClienteResponse(pessoa);
    }

    @Transactional
    public void deletar(Long id) {
        Pessoa pessoa = pessoaRepository.findById(id);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }

        pessoaRepository.delete(pessoa);
    }

    public ClienteResponse buscarPorCpf(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com CPF: " + cpf);
        }
        return pessoaMapper.toClienteResponse(pessoa);
    }

    public ClienteResponse buscarPorUsername(String username) {
        Pessoa pessoa = pessoaRepository.findByUsername(username);
        if (pessoa == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com username: " + username);
        }
        return pessoaMapper.toClienteResponse(pessoa);
    }
}

