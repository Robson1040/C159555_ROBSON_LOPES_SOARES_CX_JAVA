package br.gov.caixa.api.investimentos.model.cliente;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PessoaTest {

    @Test
    void testConstrutorPadraoEGettersSetters() {
        Pessoa pessoa = new Pessoa();

        pessoa.setId(1L);
        pessoa.setNome("Robson Lopes");
        pessoa.setCpf("12345678901");
        pessoa.setUsername("robson");
        pessoa.setPassword("senha123");
        pessoa.setRole("ADMIN");

        assertEquals(1L, pessoa.getId());
        assertEquals("Robson Lopes", pessoa.getNome());
        assertEquals("12345678901", pessoa.getCpf());
        assertEquals("robson", pessoa.getUsername());
        assertEquals("senha123", pessoa.getPassword());
        assertEquals("ADMIN", pessoa.getRole());
    }

    @Test
    void testConstrutorCompleto() {
        Pessoa pessoa = new Pessoa(
                "Robson Lopes",
                "12345678901",
                "robson",
                "senha123",
                "USER"
        );

        assertNull(pessoa.getId()); // ainda não persistido
        assertEquals("Robson Lopes", pessoa.getNome());
        assertEquals("12345678901", pessoa.getCpf());
        assertEquals("robson", pessoa.getUsername());
        assertEquals("senha123", pessoa.getPassword());
        assertEquals("USER", pessoa.getRole());
    }

    @Test
    void testToString() {
        Pessoa pessoa = new Pessoa(
                "Robson Lopes",
                "12345678901",
                "robson",
                "senha123",
                "ADMIN"
        );
        pessoa.setId(10L);

        String str = pessoa.toString();
        assertTrue(str.contains("10"));
        assertTrue(str.contains("Robson Lopes"));
        assertTrue(str.contains("12345678901"));
        assertTrue(str.contains("robson"));
        assertTrue(str.contains("ADMIN"));
    }
}