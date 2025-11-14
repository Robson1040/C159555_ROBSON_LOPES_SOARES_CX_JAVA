package org.example.repository.cliente;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.model.cliente.Pessoa;

/**
 * Repository para operações de banco de dados da entidade Pessoa
 */
@ApplicationScoped
public class PessoaRepository implements IPessoaRepository {

    /**
     * Busca pessoa por CPF
     */
    public Pessoa findByCpf(String cpf) {
        return find("cpf", cpf).firstResult();
    }

    /**
     * Busca pessoa por username
     */
    public Pessoa findByUsername(String username) {
        return find("username", username).firstResult();
    }

    /**
     * Verifica se CPF já existe
     */
    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }

    /**
     * Verifica se username já existe
     */
    public boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }
}