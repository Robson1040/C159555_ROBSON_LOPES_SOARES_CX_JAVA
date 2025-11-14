package org.example.resource.autenticacao;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.common.ErrorResponse;
import org.example.dto.autenticacao.LoginRequest;
import org.example.dto.autenticacao.LoginResponse;
import org.example.exception.cliente.ClienteNotFoundException;
import org.example.service.autenticacao.AutenticacaoService;

import java.time.LocalDateTime;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AutenticacaoResource {

    @Inject
    AutenticacaoService autenticacaoService;

    @POST
    @Path("/entrar")
    public Response entrar(@Valid LoginRequest loginRequest) {
        try {
            LoginResponse response = autenticacaoService.autenticar(loginRequest);
            return Response.ok(response).build();
            
        } catch (ClienteNotFoundException e) {
            ErrorResponse error = new ErrorResponse(
                "Credenciais inválidas",
                LocalDateTime.now(),
                401,
                "/entrar",
                null
            );
            return Response.status(401).entity(error).build();
            
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                "Erro interno do servidor: " + e.getMessage(),
                LocalDateTime.now(),
                500,
                "/entrar",
                null
            );
            return Response.status(500).entity(error).build();
        }
    }
}
