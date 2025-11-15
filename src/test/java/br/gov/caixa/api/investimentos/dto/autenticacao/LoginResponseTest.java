package br.gov.caixa.api.investimentos.dto.autenticacao;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void shouldCreateLoginResponseAndAccessFields() {
        LocalDateTime expira = LocalDateTime.now().plusHours(1);
        LoginResponse response = new LoginResponse("token123", "Bearer", expira, "usuario1", "ADMIN");

        // Testa getters
        assertEquals("token123", response.token());
        assertEquals("Bearer", response.tipo());
        assertEquals(expira, response.expiraEm());
        assertEquals("usuario1", response.usuario());
        assertEquals("ADMIN", response.role());

        // Testa toString
        String str = response.toString();
        assertTrue(str.contains("token123"));
        assertTrue(str.contains("Bearer"));
        assertTrue(str.contains("usuario1"));
        assertTrue(str.contains("ADMIN"));

        // Testa equals e hashCode
        LoginResponse sameResponse = new LoginResponse("token123", "Bearer", expira, "usuario1", "ADMIN");
        LoginResponse differentResponse = new LoginResponse("token456", "Bearer", expira, "usuario2", "USER");

        assertEquals(response, sameResponse);
        assertNotEquals(response, differentResponse);
        assertEquals(response.hashCode(), sameResponse.hashCode());
        assertNotEquals(response.hashCode(), differentResponse.hashCode());
    }
}