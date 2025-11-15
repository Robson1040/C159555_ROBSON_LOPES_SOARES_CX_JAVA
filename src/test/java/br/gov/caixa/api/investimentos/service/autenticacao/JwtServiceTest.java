package br.gov.caixa.api.investimentos.service.autenticacao;

import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void testGenerateTokenWithPessoa() {
        Pessoa usuario = new Pessoa("Robson Lopes", "12345678901", "robson", "senha123", "USER");
        usuario.setId(100L);

        String token = jwtService.generateToken(usuario);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Decodifica o payload (parte do meio do JWT)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

        assertTrue(payloadJson.contains("\"sub\":\"robson\""));
        assertTrue(payloadJson.contains("\"nome\":\"Robson Lopes\""));
        assertTrue(payloadJson.contains("\"cpf\":\"12345678901\""));
        assertTrue(payloadJson.contains("\"userId\":100"));
        assertTrue(payloadJson.contains("\"groups\":[\"USER\"]"));
    }

    @Test
    void testGerarTokenComEmailERole() {
        String email = "teste@caixa.gov.br";
        String role = "ADMIN";

        String token = jwtService.gerarToken(email, role);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

        assertTrue(payloadJson.contains("\"sub\":\"" + email + "\""));
        assertTrue(payloadJson.contains("\"email\":\"" + email + "\""));
        assertTrue(payloadJson.contains("\"groups\":[\"" + role + "\"]"));
    }
}