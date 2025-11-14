package org.example.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.ClienteRequest;
import org.example.dto.ClienteResponse;
import org.example.dto.ClienteUpdateRequest;
import org.example.service.ClienteService;

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
    public Response listarTodos() {
        List<ClienteResponse> clientes = clienteService.listarTodos();
        return Response.ok(clientes).build();
    }

    /**
     * GET /clientes/{id} - Busca cliente por ID
     */
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        ClienteResponse cliente = clienteService.buscarPorId(id);
        return Response.ok(cliente).build();
    }

    /**
     * POST /clientes - Cria um novo cliente
     */
    @POST
    public Response criar(@Valid ClienteRequest request) {
        ClienteResponse cliente = clienteService.criar(request);
        return Response.status(Response.Status.CREATED).entity(cliente).build();
    }

    /**
     * PUT /clientes/{id} - Atualiza um cliente existente
     */
    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, @Valid ClienteUpdateRequest request) {
        ClienteResponse cliente = clienteService.atualizar(id, request);
        return Response.ok(cliente).build();
    }

    /**
     * DELETE /clientes/{id} - Remove um cliente
     */
    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        clienteService.deletar(id);
        return Response.noContent().build();
    }

    /**
     * GET /clientes/cpf/{cpf} - Busca cliente por CPF
     */
    @GET
    @Path("/cpf/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        ClienteResponse cliente = clienteService.buscarPorCpf(cpf);
        return Response.ok(cliente).build();
    }

    /**
     * GET /clientes/username/{username} - Busca cliente por username
     */
    @GET
    @Path("/username/{username}")
    public Response buscarPorUsername(@PathParam("username") String username) {
        ClienteResponse cliente = clienteService.buscarPorUsername(username);
        return Response.ok(cliente).build();
    }
}