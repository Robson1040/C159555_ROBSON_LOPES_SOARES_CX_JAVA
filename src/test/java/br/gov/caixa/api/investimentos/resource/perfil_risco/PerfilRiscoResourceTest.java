package br.gov.caixa.api.investimentos.resource.perfil_risco;

import br.gov.caixa.api.investimentos.dto.perfil_risco.PerfilRiscoResponse;
import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.service.perfil_risco.PerfilRiscoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerfilRiscoResourceTest {

    private PerfilRiscoService service;
    private PerfilRiscoResource resource;

    @BeforeEach
    void setUp() {
        service = mock(PerfilRiscoService.class);
        resource = new PerfilRiscoResource();
        resource.perfilRiscoService = service; // injeta mock
    }

    @Test
    void calcularPerfilRisco_deveRetornarPerfilComSucesso() {
        Long clienteId = 1L;
        PerfilRiscoResponse responseMock = new PerfilRiscoResponse(
                clienteId,"Moderado", 50, "Perfil equilibrado entre risco e retorno"
        );

        when(service.calcularPerfilRisco(clienteId)).thenReturn(responseMock);

        Response response = resource.calcularPerfilRisco(clienteId);

        assertEquals(200, response.getStatus());
        assertEquals(responseMock, response.getEntity());
    }

    @Test
    void calcularPerfilRisco_quandoClienteNaoEncontrado_deveRetornar404() {
        Long clienteId = 2L;
        when(service.calcularPerfilRisco(clienteId)).thenThrow(new ClienteNotFoundException("Cliente não encontrado."));

        Response response = resource.calcularPerfilRisco(clienteId);

        assertEquals(404, response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals("Cliente não encontrado", entity.message());
    }

    @Test
    void calcularPerfilRisco_quandoErroIllegalState_deveRetornar400() {
        Long clienteId = 3L;
        String errorMsg = "Sem histórico de investimentos ou simulações";
        when(service.calcularPerfilRisco(clienteId)).thenThrow(new IllegalStateException(errorMsg));

        Response response = resource.calcularPerfilRisco(clienteId);

        assertEquals(400, response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(errorMsg, entity.message());
    }

    @Test
    void calcularPerfilRisco_quandoErroGenerico_deveRetornar500() {
        Long clienteId = 4L;
        when(service.calcularPerfilRisco(clienteId)).thenThrow(new RuntimeException("Erro inesperado"));

        Response response = resource.calcularPerfilRisco(clienteId);

        assertEquals(500, response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals("Erro interno no servidor", entity.message());
    }
}
