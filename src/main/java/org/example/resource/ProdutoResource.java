package org.example.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.ProdutoRequest;
import org.example.dto.ProdutoResponse;
import org.example.exception.ProdutoNotFoundException;
import org.example.model.TipoProduto;
import org.example.model.TipoRentabilidade;
import org.example.service.ProdutoService;

import java.util.List;
import java.util.Optional;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject
    ProdutoService produtoService;

    /**
     * GET /produtos - Lista todos os produtos
     * Suporta filtros opcionais via query parameters
     */
    @GET
    public Response listarProdutos(
            @QueryParam("tipo") TipoProduto tipo,
            @QueryParam("tipo_rentabilidade") TipoRentabilidade tipoRentabilidade,
            @QueryParam("fgc") Boolean fgc,
            @QueryParam("liquidez_imediata") Boolean liquidezImediata,
            @QueryParam("sem_liquidez") Boolean semLiquidez,
            @QueryParam("nome") String nome
    ) {
        try {
            List<ProdutoResponse> produtos;

            // Aplicar filtros baseados nos query parameters
            if (tipo != null) {
                produtos = produtoService.buscarPorTipo(tipo);
            } else if (tipoRentabilidade != null) {
                produtos = produtoService.buscarPorTipoRentabilidade(tipoRentabilidade);
            } else if (Boolean.TRUE.equals(fgc)) {
                produtos = produtoService.buscarProdutosComFgc();
            } else if (Boolean.TRUE.equals(liquidezImediata)) {
                produtos = produtoService.buscarProdutosComLiquidezImediata();
            } else if (Boolean.TRUE.equals(semLiquidez)) {
                produtos = produtoService.buscarProdutosSemLiquidez();
            } else if (nome != null && !nome.trim().isEmpty()) {
                produtos = produtoService.buscarPorNome(nome);
            } else {
                produtos = produtoService.listarTodos();
            }

            if (produtos.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(produtos).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro interno do servidor: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /produtos/{id} - Busca produto por ID
     */
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("ID é obrigatório"))
                        .build();
            }

            Optional<ProdutoResponse> produto = produtoService.buscarPorId(id);
            
            if (produto.isPresent()) {
                return Response.ok(produto.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Produto não encontrado com ID: " + id))
                        .build();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro interno do servidor: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /produtos - Cria um novo produto
     */
    @POST
    public Response criarProduto(@Valid @NotNull ProdutoRequest request) {
        try {
            ProdutoResponse produto = produtoService.criar(request);
            return Response.status(Response.Status.CREATED)
                    .entity(produto)
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Dados inválidos: " + e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro interno do servidor: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * PUT /produtos/{id} - Atualiza um produto existente
     */
    @PUT
    @Path("/{id}")
    public Response atualizarProduto(@PathParam("id") Long id, @Valid @NotNull ProdutoRequest request) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("ID é obrigatório"))
                        .build();
            }

            ProdutoResponse produto = produtoService.atualizar(id, request);
            return Response.ok(produto).build();

        } catch (ProdutoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Dados inválidos: " + e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro interno do servidor: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * DELETE /produtos/{id} - Remove um produto
     */
    @DELETE
    @Path("/{id}")
    public Response removerProduto(@PathParam("id") Long id) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("ID é obrigatório"))
                        .build();
            }

            produtoService.remover(id);
            return Response.status(Response.Status.NO_CONTENT).build();

        } catch (ProdutoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro interno do servidor: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /produtos/count - Conta total de produtos
     */
    @GET
    @Path("/count")
    public Response contarProdutos() {
        try {
            long total = produtoService.contarTodos();
            return Response.ok(new CountResponse(total)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro interno do servidor: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Classe para respostas de erro
     */
    public static class ErrorResponse {
        private String message;

        public ErrorResponse() {}

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * Classe para resposta de contagem
     */
    public static class CountResponse {
        private long total;

        public CountResponse() {}

        public CountResponse(long total) {
            this.total = total;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }
}