package br.gov.caixa.api.investimentos.resource.perfil_risco;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.gov.caixa.api.investimentos.dto.perfil_risco.PerfilRiscoResponse;
import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.service.perfil_risco.PerfilRiscoService;

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
                    .entity(ErrorResponse.notFound("Cliente não encontrado"))
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponse.badRequest(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponse.internalError("Erro interno no servidor"))
                    .build();
        }
    }
}

