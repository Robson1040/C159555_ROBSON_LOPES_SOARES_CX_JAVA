package br.gov.caixa.api.investimentos.resource.produto_recomendado;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.service.produto_recomendado.ProdutoRecomendadoService;

import java.util.List;

/**
 * Resource REST para recomendação de produtos baseado no perfil de risco
 */
@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER", "ADMIN"})
public class ProdutoRecomendadoResource {

    @Inject
    ProdutoRecomendadoService produtoRecomendadoService;

    /**
     * GET /produtos-recomendados/{perfil} - Retorna produtos recomendados baseado no perfil de risco
     * 
     * Perfis aceitos:
     * - Conservador: produtos de risco BAIXO (com garantia FGC)
     * - Moderado: produtos de risco MÉDIO (renda fixa sem FGC)
     * - Agressivo: produtos de risco ALTO (renda variável sem FGC)
     * 
     * @param perfil Perfil de risco do cliente (Conservador, Moderado, Agressivo)
     * @return Lista de produtos recomendados
     */
    @GET
    @Path("/{perfil}")
    public Response buscarProdutosPorPerfil(@PathParam("perfil") String perfil) {
        try {
            List<ProdutoResponse> produtos = produtoRecomendadoService.buscarProdutosPorPerfil(perfil);

            return Response.ok(produtos).build();
            
        } catch (IllegalArgumentException e) {
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

