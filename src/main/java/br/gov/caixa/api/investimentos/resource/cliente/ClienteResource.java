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

    
    @GET
    @RolesAllowed({"ADMIN"})
    public Response listarTodos() {
        List<ClienteResponse> clientes = clienteService.listarTodos();
        return Response.ok(clientes).build();
    }

    
    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response buscarPorId(@PathParam("id") Long id) {
        
        authHelper.validarAcessoAoCliente(jwt, id);
        
        ClienteResponse cliente = clienteService.buscarPorId(id);
        return Response.ok(cliente).build();
    }

    
    @POST
    @PermitAll
    public Response criar(@Valid ClienteRequest request) {
        ClienteResponse cliente = clienteService.criar(request);
        return Response.status(Response.Status.CREATED).entity(cliente).build();
    }

    
    @PUT
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response atualizar(@PathParam("id") Long id, @Valid ClienteUpdateRequest request) {
        
        authHelper.validarAcessoAoCliente(jwt, id);
        
        ClienteResponse cliente = clienteService.atualizar(id, request);
        return Response.ok(cliente).build();
    }

    
    @GET
    @Path("/cpf/{cpf}")
    @RolesAllowed({"ADMIN"})
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        ClienteResponse cliente = clienteService.buscarPorCpf(cpf);
        return Response.ok(cliente).build();
    }

    
    @GET
    @Path("/username/{username}")
    @RolesAllowed({"ADMIN"})
    public Response buscarPorUsername(@PathParam("username") String username) {
        ClienteResponse cliente = clienteService.buscarPorUsername(username);
        return Response.ok(cliente).build();
    }
}
