package org.example.resource.perfil_risco;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.perfil_risco.PerfilRiscoResponse;
import org.example.exception.cliente.ClienteNotFoundException;
import org.example.service.perfil_risco.PerfilRiscoService;

/**
 * Resource REST para operações relacionadas ao perfil de risco do cliente
 */
@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER", "ADMIN"})
public class PerfilRiscoResource {

    @Inject
    PerfilRiscoService perfilRiscoService;

    /**
     * GET /perfil-risco/{clienteId} - Calcula e retorna o perfil de risco do cliente
     * 
     * O perfil é calculado baseado no histórico de investimentos do cliente.
     * Se não houver investimentos, usa o histórico de simulações.
     * Se não houver nenhum histórico, retorna erro 400.
     * 
     * @param clienteId ID do cliente para calcular o perfil
     * @return PerfilRiscoResponse com perfil, pontuação e descrição
     */
    @GET
    @Path("/{clienteId}")
    public Response calcularPerfilRisco(@PathParam("clienteId") Long clienteId) {
        try {
            PerfilRiscoResponse perfil = perfilRiscoService.calcularPerfilRisco(clienteId);
            return Response.ok(perfil).build();
        } catch (ClienteNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Cliente não encontrado"))
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro interno no servidor"))
                    .build();
        }
    }

    /**
     * DTO para respostas de erro
     */
    public record ErrorResponse(String message) {}
}

