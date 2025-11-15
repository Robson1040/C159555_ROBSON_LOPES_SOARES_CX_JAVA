package br.gov.caixa.api.investimentos.repository.cliente;

import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

/**
 * Interface para operações de banco de dados da entidade Pessoa
 */
public interface IPessoaRepository extends PanacheRepository<Pessoa> {

    /**
     * Busca pessoa por CPF
     */
    Pessoa findByCpf(String cpf);

    /**
     * Busca pessoa por username
     */
    Pessoa findByUsername(String username);

    /**
     * Verifica se CPF já existe
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se username já existe
     */
    boolean existsByUsername(String username);
}