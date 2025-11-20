package br.gov.caixa.api.investimentos.resource.cliente;

import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteUpdateRequest;
import br.gov.caixa.api.investimentos.helper.auth.JwtAuthorizationHelper;
import br.gov.caixa.api.investimentos.service.cliente.ClienteService;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteResourceTest {

    private ClienteService service;
    private JwtAuthorizationHelper authHelper;
    private JsonWebToken jwt;
    private ClienteResource resource;

    @BeforeEach
    void setUp() {
        service = mock(ClienteService.class);
        authHelper = mock(JwtAuthorizationHelper.class);
        jwt = mock(JsonWebToken.class);

        resource = new ClienteResource();
        resource.clienteService = service; // Injeta mock manualmente
        resource.authHelper = authHelper; // Injeta mock manualmente
        resource.jwt = jwt; // Injeta mock manualmente

        // Mock padrão para JWT - ADMIN com acesso total
        when(jwt.getGroups()).thenReturn(Set.of("ADMIN"));
    }

    @Test
    void listarTodos_deveRetornarLista() {
        ClienteResponse cliente1 = new ClienteResponse(1L, "João", "12345678900", "joao", "USER");
        ClienteResponse cliente2 = new ClienteResponse(2L, "Maria", "98765432100", "maria", "ADMIN");
        when(service.listarTodos()).thenReturn(List.of(cliente1, cliente2));

        Response response = resource.listarTodos();
        assertEquals(200, response.getStatus());
        List<?> result = (List<?>) response.getEntity();
        assertEquals(2, result.size());
        assertTrue(result.contains(cliente1));
        assertTrue(result.contains(cliente2));
    }

    @Test
    void buscarPorId_deveRetornarCliente() {
        ClienteResponse cliente = new ClienteResponse(1L, "João", "12345678900", "joao", "USER");
        when(service.buscarPorId(1L)).thenReturn(cliente);

        Response response = resource.buscarPorId(1L);
        assertEquals(200, response.getStatus());
        assertEquals(cliente, response.getEntity());
    }

    @Test
    void criar_deveRetornarClienteCriado() {
        ClienteRequest request = new ClienteRequest("João", "12345678900", "joao", "senha", "USER");
        ClienteResponse cliente = new ClienteResponse(1L, "João", "12345678900", "joao", "USER");
        when(service.criar(request)).thenReturn(cliente);

        Response response = resource.criar(request);
        assertEquals(201, response.getStatus());
        assertEquals(cliente, response.getEntity());
    }

    @Test
    void atualizar_deveRetornarClienteAtualizado() {
        ClienteUpdateRequest request = new ClienteUpdateRequest("João Atualizado", "joaoatualizado", "senha123456");
        ClienteResponse cliente = new ClienteResponse(1L, "João Atualizado", "12345678900", "joaoatualizado", "USER");
        when(service.atualizar(1L, request)).thenReturn(cliente);

        Response response = resource.atualizar(1L, request);
        assertEquals(200, response.getStatus());
        assertEquals(cliente, response.getEntity());
    }

    @Test
    void buscarPorCpf_deveRetornarCliente() {
        ClienteResponse cliente = new ClienteResponse(1L, "João", "12345678900", "joao", "USER");
        when(service.buscarPorCpf("12345678900")).thenReturn(cliente);

        Response response = resource.buscarPorCpf("12345678900");
        assertEquals(200, response.getStatus());
        assertEquals(cliente, response.getEntity());
    }

    @Test
    void buscarPorUsername_deveRetornarCliente() {
        ClienteResponse cliente = new ClienteResponse(1L, "João", "12345678900", "joao", "USER");
        when(service.buscarPorUsername("joao")).thenReturn(cliente);

        Response response = resource.buscarPorUsername("joao");
        assertEquals(200, response.getStatus());
        assertEquals(cliente, response.getEntity());
    }
}