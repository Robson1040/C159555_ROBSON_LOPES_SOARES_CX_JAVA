package org.example.test.integration;

import io.quarkus.test.junit.QuarkusTest;
import org.example.model.Pessoa;
import org.example.model.Produto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ImportSqlTest {

    @Test
    public void testDadosPessoas() {
        List<Pessoa> pessoas = Pessoa.listAll();
        
        System.out.println("Total de pessoas encontradas: " + pessoas.size());
        
        // Verificar se as pessoas do import.sql foram carregadas
        assertEquals(2, pessoas.size(), "Deve haver 2 pessoas carregadas do import.sql");
        
        // Verificar dados específicos
        boolean adminEncontrado = pessoas.stream()
            .anyMatch(p -> "admin".equals(p.username) && "Admin Test".equals(p.nome));
        boolean userEncontrado = pessoas.stream()
            .anyMatch(p -> "user".equals(p.username) && "User Test".equals(p.nome));
            
        assertTrue(adminEncontrado, "Usuário admin deve estar presente");
        assertTrue(userEncontrado, "Usuário user deve estar presente");
    }

    @Test
    public void testDadosProdutos() {
        List<Produto> produtos = Produto.listAll();
        
        System.out.println("Total de produtos encontrados: " + produtos.size());
        produtos.forEach(p -> System.out.println("Produto: " + p.getNome()));
        
        // Verificar se os produtos do import.sql foram carregados
        assertEquals(5, produtos.size(), "Deve haver 5 produtos carregados do import.sql");
        
        // Verificar produtos específicos
        boolean tesouroDiretoEncontrado = produtos.stream()
            .anyMatch(p -> "Tesouro IPCA".equals(p.getNome()));
        boolean cdbEncontrado = produtos.stream()
            .anyMatch(p -> "CDB Banco XYZ".equals(p.getNome()));
            
        assertTrue(tesouroDiretoEncontrado, "Produto Tesouro IPCA deve estar presente");
        assertTrue(cdbEncontrado, "Produto CDB Banco XYZ deve estar presente");
    }


}