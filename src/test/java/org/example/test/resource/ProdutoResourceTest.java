package org.example.test.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProdutoResourceTest {

    // @BeforeEach
    // @Transactional
    // public void setUp() {
    //     // Limpar dados antes de cada teste
    //     Produto.deleteAll();
    // }

    @Test
    public void testListarProdutosComDados() {
        given()
                .when().get("/produtos")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)); // Deve retornar produtos do import.sql
    }

    @Test
    public void testCriarProduto() {
        String produtoJson = """
                {
                    "nome": "CDB Premium",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.2,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "CDI",
                    "liquidez": 30,
                    "minimo_dias_investimento": 90,
                    "fgc": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(produtoJson)
                .when().post("/produtos")
                .then()
                .statusCode(201) // CREATED
                .body("nome", is("CDB Premium"))
                .body("tipo", is("CDB"))
                .body("tipo_rentabilidade", is("POS"))
                .body("rentabilidade", is(1.2f))
                .body("indice", is("CDI"))
                .body("liquidez", is(30))
                .body("fgc", is(true));
    }

    @Test
    public void testCriarProdutoComValidacaoErro() {
        String produtoInvalido = """
                {
                    "nome": "",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": -1,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "CDI",
                    "liquidez": 30,
                    "minimo_dias_investimento": 90,
                    "fgc": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(produtoInvalido)
                .when().post("/produtos")
                .then()
                .statusCode(400); // BAD_REQUEST
    }



    @Test
    public void testBuscarProdutoPorId() {
        // Criar produto de teste
        Integer produtoId = criarProdutoTeste();

        given()
                .when().get("/produtos/" + produtoId)
                .then()
                .statusCode(200)
                .body("nome", is("Tesouro IPCA"))
                .body("id", is(produtoId));
    }

    @Test
    public void testBuscarProdutoPorIdNaoExistente() {
        given()
                .when().get("/produtos/99999")
                .then()
                .statusCode(404)
                .body("message", containsString("não encontrado"));
    }

    @Test
    public void testFiltrarProdutosPorTipo() {
        // Criar produtos de diferentes tipos
        criarProdutoTeste(); // TESOURO_DIRETO
        criarCdbTeste();     // CDB

        given()
                .queryParam("tipo", "CDB")
                .when().get("/produtos")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].tipo", is("CDB"));
    }

    @Test
    public void testFiltrarProdutosPorFgc() {
        // Criar produtos com e sem FGC
        criarProdutoTeste(); // com FGC
        criarCdbTeste();     // com FGC

        given()
                .queryParam("fgc", true)
                .when().get("/produtos")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("every { it.fgc == true }", is(true));
    }

    @Test
    public void testAtualizarProduto() {
        Integer produtoId = criarProdutoTeste();

        String produtoAtualizado = """
                {
                    "nome": "Tesouro IPCA Atualizado",
                    "tipo": "TESOURO_DIRETO",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 6.5,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "IPCA",
                    "liquidez": 1,
                    "minimo_dias_investimento": 30,
                    "fgc": false
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(produtoAtualizado)
                .when().put("/produtos/" + produtoId)
                .then()
                .statusCode(200)
                .body("nome", is("Tesouro IPCA Atualizado"))
                .body("rentabilidade", is(6.5f));
    }

    @Test
    public void testRemoverProduto() {
        Integer produtoId = criarProdutoTeste();

        given()
                .when().delete("/produtos/" + produtoId)
                .then()
                .statusCode(204); // NO_CONTENT

        // Verificar se foi removido
        given()
                .when().get("/produtos/" + produtoId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testContarProdutos() {
        criarProdutoTeste();
        criarCdbTeste();

        given()
                .when().get("/produtos/count")
                .then()
                .statusCode(200)
                .body("total", is(2));
    }

    @Test
    public void testValidacaoRegrasPosFixado() {
        // Produto pós-fixado deve ter índice diferente de NENHUM
        String produtoInvalido = """
                {
                    "nome": "CDB Inválido",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.2,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "NENHUM",
                    "liquidez": 30,
                    "minimo_dias_investimento": 90,
                    "fgc": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(produtoInvalido)
                .when().post("/produtos")
                .then()
                .statusCode(400)
                .body("message", containsString("pós-fixados devem ter um índice válido"));
    }

    // Métodos auxiliares - usar API REST para criar produtos nos testes
    private Integer criarProdutoTeste() {
        String produtoJson = """
                {
                    "nome": "Tesouro IPCA",
                    "tipo": "TESOURO_DIRETO",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 6.0,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "IPCA",
                    "liquidez": 1,
                    "minimo_dias_investimento": 0,
                    "fgc": true
                }
                """;

        return given()
                .contentType(ContentType.JSON)
                .body(produtoJson)
                .when().post("/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    private Integer criarCdbTeste() {
        String produtoJson = """
                {
                    "nome": "CDB Banco XYZ",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.15,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "CDI",
                    "liquidez": 90,
                    "minimo_dias_investimento": 180,
                    "fgc": true
                }
                """;

        return given()
                .contentType(ContentType.JSON)
                .body(produtoJson)
                .when().post("/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");
    }
}