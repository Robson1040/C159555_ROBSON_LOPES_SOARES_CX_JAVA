package br.gov.caixa.api.investimentos.repository.cliente;

import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PessoaRepository implements IPessoaRepository {

    public Pessoa findByCpf(String cpf) {
        return find("cpf", cpf).firstResult();
    }

    public Pessoa findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }

    public boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }
}