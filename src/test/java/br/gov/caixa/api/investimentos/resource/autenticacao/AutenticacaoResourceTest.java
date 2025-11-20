package br.gov.caixa.api.investimentos.resource.autenticacao;

import br.gov.caixa.api.investimentos.dto.autenticacao.LoginRequest;
import br.gov.caixa.api.investimentos.dto.autenticacao.LoginResponse;
import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.service.autenticacao.AutenticacaoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AutenticacaoResourceTest {

    private AutenticacaoService service;
    private AutenticacaoResource resource;

    @BeforeEach
    void setUp() {
        service = mock(AutenticacaoService.class);
        resource = new AutenticacaoResource();
        resource.autenticacaoService = service; // Injeta mock manualmente
    }

    @Test
    void entrar_sucesso_retorna200() {
        LoginRequest request = new LoginRequest("usuario", "senha");

        // LoginResponse completo (record exige todos os campos)
        LoginResponse loginResponse = new LoginResponse(
                "token123",                    // token
                "Bearer",                      // tipo
                LocalDateTime.now().plusHours(1), // expiraEm
                "usuario",                     // usuario
                "USER"                         // role
        );

        when(service.autenticar(request)).thenReturn(loginResponse);

        Response response = resource.entrar(request);

        assertEquals(200, response.getStatus());
        assertEquals(loginResponse, response.getEntity());
    }

    @Test
    void entrar_clienteNaoEncontrado_retorna401() {
        LoginRequest request = new LoginRequest("usuario", "senha");
        when(service.autenticar(request)).thenThrow(new ClienteNotFoundException("Credenciais inválidas"));

        Response response = resource.entrar(request);

        assertEquals(401, response.getStatus());
        assertTrue(response.getEntity() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getEntity();
        assertEquals("Credenciais inválidas", error.message());
        assertEquals("/entrar", error.path());
        assertNotNull(error.timestamp());
    }

    @Test
    void entrar_erroGenerico_retorna500() {
        LoginRequest request = new LoginRequest("usuario", "senha");
        when(service.autenticar(request)).thenThrow(new RuntimeException("Falha inesperada"));

        Response response = resource.entrar(request);

        assertEquals(500, response.getStatus());
        assertTrue(response.getEntity() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getEntity();
        assertTrue(error.message().contains("Falha inesperada"));
        assertEquals("/entrar", error.path());
        assertNotNull(error.timestamp());
    }
}