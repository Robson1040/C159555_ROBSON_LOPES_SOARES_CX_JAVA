package br.gov.caixa.api.investimentos.repository.cliente;

import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

public interface IPessoaRepository extends PanacheRepository<Pessoa> {

    Pessoa findByCpf(String cpf);

    Pessoa findByUsername(String username);

    boolean existsByCpf(String cpf);

    boolean existsByUsername(String username);
}