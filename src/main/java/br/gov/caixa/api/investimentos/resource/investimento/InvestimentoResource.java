package br.gov.caixa.api.investimentos.resource.investimento;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoResponse;
import br.gov.caixa.api.investimentos.service.investimento.InvestimentoService;

import java.util.List;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER", "ADMIN"})
public class InvestimentoResource {

    @Inject
    InvestimentoService investimentoService;

    /**
     * POST /investimentos - registra um novo investimento
     */
    @POST
    public Response criar(@Valid @NotNull InvestimentoRequest request) {
        InvestimentoResponse response = investimentoService.criar(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    /**
     * GET /investimentos/{clienteId} - busca todos os investimentos de um cliente
     */
    @GET
    @Path("/{clienteId}")
    public Response buscarPorCliente(@PathParam("clienteId") @NotNull Long clienteId) {
        List<InvestimentoResponse> investimentos = investimentoService.buscarPorCliente(clienteId);

        return Response.ok(investimentos).build();
    }
}


