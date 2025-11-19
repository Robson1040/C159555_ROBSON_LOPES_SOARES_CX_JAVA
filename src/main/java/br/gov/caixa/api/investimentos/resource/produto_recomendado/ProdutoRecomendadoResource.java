package br.gov.caixa.api.investimentos.resource.produto_recomendado;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import br.gov.caixa.api.investimentos.service.produto_recomendado.ProdutoRecomendadoService;
import br.gov.caixa.api.investimentos.helper.auth.JwtAuthorizationHelper;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;


@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER", "ADMIN"})
public class ProdutoRecomendadoResource {

    @Inject
    ProdutoRecomendadoService produtoRecomendadoService;

    @Inject
    JsonWebToken jwt;

    @Inject
    JwtAuthorizationHelper authHelper;

    
    @GET
    @Path("/cliente/{clienteId}")
    public Response buscarProdutosPorCliente(@PathParam("clienteId") @Positive Long clienteId) {
        try {
            
            authHelper.validarAcessoAoCliente(jwt, clienteId);

            List<ProdutoResponse> produtos = produtoRecomendadoService.buscarProdutosPorCliente(clienteId);

            return Response.ok(produtos).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponse.badRequest(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponse.badRequest(e.getMessage()))
                    .build();
        } catch (br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponse.notFound(e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Erro interno no ProdutoRecomendadoResource: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponse.internalError("Erro interno no servidor"))
                    .build();
        }
    }

    
    @GET
    @Path("/{perfil}")
    public Response buscarProdutosPorPerfil(@PathParam("perfil") String perfil) {
        try {
            List<ProdutoResponse> produtos = produtoRecomendadoService.buscarProdutosPorPerfil(perfil);

            return Response.ok(produtos).build();
            
        } catch (IllegalArgumentException e) {
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

