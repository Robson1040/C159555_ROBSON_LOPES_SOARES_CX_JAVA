package br.gov.caixa.api.investimentos.repository.cliente;

import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PessoaRepositoryTest {

    private PessoaRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new PessoaRepository());
    }

    @Test
    void findByCpf_shouldReturnPessoa() {
        Pessoa pessoa = new Pessoa();
        // Mock de PanacheQuery
        PanacheQuery<Pessoa> query = mock(PanacheQuery.class);
        when(query.firstResult()).thenReturn(pessoa);
        doReturn(query).when(repository).find("cpf", "12345678900");

        Pessoa result = repository.findByCpf("12345678900");
        assertNotNull(result);
        assertEquals(pessoa, result);
    }

    @Test
    void findByUsername_shouldReturnPessoa() {
        Pessoa pessoa = new Pessoa();
        PanacheQuery<Pessoa> query = mock(PanacheQuery.class);
        when(query.firstResult()).thenReturn(pessoa);
        doReturn(query).when(repository).find("username", "joao");

        Pessoa result = repository.findByUsername("joao");
        assertNotNull(result);
        assertEquals(pessoa, result);
    }

    @Test
    void existsByCpf_shouldReturnTrueOrFalse() {
        doReturn(1L).when(repository).count("cpf", "12345678900");
        assertTrue(repository.existsByCpf("12345678900"));

        doReturn(0L).when(repository).count("cpf", "00000000000");
        assertFalse(repository.existsByCpf("00000000000"));
    }

    @Test
    void existsByUsername_shouldReturnTrueOrFalse() {
        doReturn(1L).when(repository).count("username", "joao");
        assertTrue(repository.existsByUsername("joao"));

        doReturn(0L).when(repository).count("username", "maria");
        assertFalse(repository.existsByUsername("maria"));
    }
}