package org.example.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.produto.ProdutoRequest;
import org.example.dto.produto.ProdutoResponse;
import org.example.exception.ProdutoNotFoundException;
import org.example.enums.TipoProduto;
import org.example.enums.TipoRentabilidade;
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
    }

    /**
     * GET /produtos/{id} - Busca produto por ID
     */
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") @NotNull Long id) {
        Optional<ProdutoResponse> produto = produtoService.buscarPorId(id);
        
        if (produto.isPresent()) {
            return Response.ok(produto.get()).build();
        } else {
            throw new ProdutoNotFoundException("Produto não encontrado com ID: " + id);
        }
    }

    /**
     * POST /produtos - Cria um novo produto
     */
    @POST
    public Response criarProduto(@Valid @NotNull ProdutoRequest request) {
        ProdutoResponse produto = produtoService.criar(request);
        return Response.status(Response.Status.CREATED)
                .entity(produto)
                .build();
    }

    /**
     * PUT /produtos/{id} - Atualiza um produto existente
     */
    @PUT
    @Path("/{id}")
    public Response atualizarProduto(@PathParam("id") @NotNull Long id, @Valid @NotNull ProdutoRequest request) {
        ProdutoResponse produto = produtoService.atualizar(id, request);
        return Response.ok(produto).build();
    }

    /**
     * DELETE /produtos/{id} - Remove um produto
     */
    @DELETE
    @Path("/{id}")
    public Response removerProduto(@PathParam("id") @NotNull Long id) {
        produtoService.remover(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * DELETE /produtos - Remove todos os produtos (para testes)
     */
    @DELETE
    public Response limparProdutos() {
        produtoService.limparTodos();
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * GET /produtos/count - Conta total de produtos
     */
    @GET
    @Path("/count")
    public Response contarProdutos() {
        long total = produtoService.contarTodos();
        return Response.ok(new CountResponse(total)).build();
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