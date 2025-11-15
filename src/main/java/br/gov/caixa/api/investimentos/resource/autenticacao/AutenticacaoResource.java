package br.gov.caixa.api.investimentos.resource.autenticacao;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import br.gov.caixa.api.investimentos.dto.autenticacao.LoginRequest;
import br.gov.caixa.api.investimentos.dto.autenticacao.LoginResponse;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.service.autenticacao.AutenticacaoService;

import java.time.LocalDateTime;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AutenticacaoResource {

    @Inject
    AutenticacaoService autenticacaoService;

    @POST
    @PermitAll
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
