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
import br.gov.caixa.api.investimentos.helper.auth.JwtAuthorizationHelper;
import org.eclipse.microprofile.jwt.JsonWebToken;

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

    @Inject
    JsonWebToken jwt;

    @Inject
    JwtAuthorizationHelper authHelper;

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
     * ADMIN pode buscar qualquer cliente, USER só pode buscar seus próprios dados
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response buscarPorId(@PathParam("id") Long id) {
        // Verificar autorização baseada no JWT
        authHelper.validarAcessoAoCliente(jwt, id);
        
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
     * USER só pode atualizar seus próprios dados
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response atualizar(@PathParam("id") Long id, @Valid ClienteUpdateRequest request) {
        // Verificar autorização baseada no JWT
        authHelper.validarAcessoAoCliente(jwt, id);
        
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
