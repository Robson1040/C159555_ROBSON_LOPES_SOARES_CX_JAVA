package org.example.test.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.example.service.autenticacao.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para JwtService - validação de tokens JWT
 * Testa geração e validação de tokens sem dependência de banco de dados
 */
@QuarkusTest
public class JwtServiceTest {

    @Inject
    JwtService jwtService;

    @Test
    public void testGerarToken() {
        String token = jwtService.gerarToken("usuario@teste.com", "USER");
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes separadas por ponto
    }

    @Test
    public void testValidarTokenValido() {
        String token = jwtService.gerarToken("usuario@teste.com", "USER");
        
        boolean valido = jwtService.validarToken(token);
        
        assertTrue(valido);
    }

    @Test
    public void testValidarTokenInvalido() {
        String tokenInvalido = "token.invalido.aqui";
        
        boolean valido = jwtService.validarToken(tokenInvalido);
        
        assertFalse(valido);
    }

    @Test
    public void testExtrairEmailDoToken() {
        String email = "usuario@teste.com";
        String token = jwtService.gerarToken(email, "USER");
        
        String emailExtraido = jwtService.extrairEmailDoToken(token);
        
        assertEquals(email, emailExtraido);
    }

    @Test
    public void testExtrairRoleDoToken() {
        String role = "ADMIN";
        String token = jwtService.gerarToken("usuario@teste.com", role);
        
        String roleExtraida = jwtService.extrairRoleDoToken(token);
        
        assertEquals(role, roleExtraida);
    }

    @Test
    public void testTokenComRolesDiferentes() {
        String tokenUser = jwtService.gerarToken("user@teste.com", "USER");
        String tokenAdmin = jwtService.gerarToken("admin@teste.com", "ADMIN");
        
        assertEquals("USER", jwtService.extrairRoleDoToken(tokenUser));
        assertEquals("ADMIN", jwtService.extrairRoleDoToken(tokenAdmin));
        
        assertTrue(jwtService.validarToken(tokenUser));
        assertTrue(jwtService.validarToken(tokenAdmin));
    }
}
