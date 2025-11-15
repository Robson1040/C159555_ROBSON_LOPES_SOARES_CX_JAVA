package br.gov.caixa.api.investimentos.resource.cliente;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteUpdateRequest;
import br.gov.caixa.api.investimentos.service.cliente.ClienteService;

import java.util.List;

/**
 * Resource REST para operações CRUD de clientes
 */
@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    /**
     * GET /clientes - Lista todos os clientes
     */
    @GET
    @RolesAllowed({"ADMIN"})
    public Response listarTodos() {
        List<ClienteResponse> clientes = clienteService.listarTodos();
        return Response.ok(clientes).build();
    }

    /**
     * GET /clientes/{id} - Busca cliente por ID
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response buscarPorId(@PathParam("id") Long id) {
        ClienteResponse cliente = clienteService.buscarPorId(id);
        return Response.ok(cliente).build();
    }

    /**
     * POST /clientes - Cria um novo cliente
     */
    @POST
    @PermitAll
    public Response criar(@Valid ClienteRequest request) {
        ClienteResponse cliente = clienteService.criar(request);
        return Response.status(Response.Status.CREATED).entity(cliente).build();
    }

    /**
     * PUT /clientes/{id} - Atualiza um cliente existente
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed({"USER"})
    public Response atualizar(@PathParam("id") Long id, @Valid ClienteUpdateRequest request) {
        ClienteResponse cliente = clienteService.atualizar(id, request);
        return Response.ok(cliente).build();
    }

    /**
     * GET /clientes/cpf/{cpf} - Busca cliente por CPF
     */
    @GET
    @Path("/cpf/{cpf}")
    @RolesAllowed({"ADMIN"})
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        ClienteResponse cliente = clienteService.buscarPorCpf(cpf);
        return Response.ok(cliente).build();
    }

    /**
     * GET /clientes/username/{username} - Busca cliente por username
     */
    @GET
    @Path("/username/{username}")
    @RolesAllowed({"ADMIN"})
    public Response buscarPorUsername(@PathParam("username") String username) {
        ClienteResponse cliente = clienteService.buscarPorUsername(username);
        return Response.ok(cliente).build();
    }
}
