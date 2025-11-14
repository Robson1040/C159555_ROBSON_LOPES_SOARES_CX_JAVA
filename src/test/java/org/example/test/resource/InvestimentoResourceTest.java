package org.example.test.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.example.model.investimento.Investimento;
import org.example.model.cliente.Pessoa;
import org.example.model.produto.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class InvestimentoResourceTest {

    @BeforeEach
    @Transactional
    public void setUp() {
        Investimento.deleteAll();
        Produto.deleteAll();
        Pessoa.deleteAll();
    }

    @Test
    public void testCriarInvestimentoSucesso() {
        Long clienteId = criarClienteTeste();
        Long produtoId = criarProdutoTeste(90); // minimo dias 90

        String json = """
                {
                  "clienteId": %d,
                  "produtoId": %d,
                  "valor": 5000.00,
                  "prazoMeses": 12,
                  "data": "2025-01-15"
                }
                """.formatted(clienteId, produtoId);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when().post("/investimentos")
                .then()
                .statusCode(201)
                .body("clienteId", is(clienteId.intValue()))
                .body("produtoId", is(produtoId.intValue()))
                .body("valor", is(5000.00f))
                .body("prazoMeses", is(12))
                .body("tipo", notNullValue())
                .body("rentabilidade", notNullValue())
                .body("fgc", notNullValue());
    }

    @Test
    public void testCriarInvestimentoProdutoNaoExiste() {
        Long clienteId = criarClienteTeste();
        String json = """
                {
                  "clienteId": %d,
                  "produtoId": 99999,
                  "valor": 1000.00,
                  "prazoMeses": 6,
                  "data": "2025-01-15"
                }
                """.formatted(clienteId);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when().post("/investimentos")
                .then()
                .statusCode(404)
                .body("message", containsString("Produto não encontrado"));
    }

    @Test
    public void testCriarInvestimentoClienteNaoExiste() {
        Long produtoId = criarProdutoTeste(0);
        String json = """
                {
                  "clienteId": 99999,
                  "produtoId": %d,
                  "valor": 1000.00,
                  "prazoMeses": 6,
                  "data": "2025-01-15"
                }
                """.formatted(produtoId);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when().post("/investimentos")
                .then()
                .statusCode(404)
                .body("message", containsString("Cliente não encontrado"));
    }

    @Test
    public void testCriarInvestimentoPrazoMenorQueMinimo() {
        Long clienteId = criarClienteTeste();
        Long produtoId = criarProdutoTeste(180); // produto exige 180 dias
        String json = """
                {
                  "clienteId": %d,
                  "produtoId": %d,
                  "valor": 1000.00,
                  "prazoDias": 30,
                  "data": "2025-01-15"
                }
                """.formatted(clienteId, produtoId);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when().post("/investimentos")
                .then()
                .statusCode(400)
                .body("message", containsString("Prazo informado é menor"));
    }

    @Test
    public void testCriarInvestimentoSemPrazo() {
        Long clienteId = criarClienteTeste();
        Long produtoId = criarProdutoTeste(0);
        String json = """
                {
                  "clienteId": %d,
                  "produtoId": %d,
                  "valor": 1000.00,
                  "data": "2025-01-15"
                }
                """.formatted(clienteId, produtoId);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when().post("/investimentos")
                .then()
                .statusCode(400)
                .body("message", containsString("Informe pelo menos um prazo"));
    }

    @Test
    public void testBuscarInvestimentosPorCliente() {
        Long clienteId = criarClienteTeste();
        Long produtoId = criarProdutoTeste(0);
        
        // Criar dois investimentos para o cliente
        String json1 = """
                {
                  "clienteId": %d,
                  "produtoId": %d,
                  "valor": 5000.00,
                  "prazoMeses": 12,
                  "data": "2025-01-15"
                }
                """.formatted(clienteId, produtoId);
                
        String json2 = """
                {
                  "clienteId": %d,
                  "produtoId": %d,
                  "valor": 3000.00,
                  "prazoDias": 180,
                  "data": "2025-03-10"
                }
                """.formatted(clienteId, produtoId);

        given()
                .contentType(ContentType.JSON)
                .body(json1)
                .when().post("/investimentos")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(json2)
                .when().post("/investimentos")
                .then()
                .statusCode(201);

        // Buscar investimentos do cliente
        given()
                .when().get("/investimentos/" + clienteId)
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].clienteId", is(clienteId.intValue()))
                .body("[1].clienteId", is(clienteId.intValue()))
                .body("[0].tipo", notNullValue())
                .body("[0].valor", notNullValue())
                .body("[0].data", notNullValue())
                .body("[0].tipo_rentabilidade", notNullValue())
                .body("[0].rentabilidade", notNullValue())
                .body("[0].periodo_rentabilidade", notNullValue())
                .body("[0].liquidez", notNullValue())
                .body("[0].minimo_dias_investimento", notNullValue())
                .body("[0].fgc", notNullValue());
    }

    @Test
    public void testBuscarInvestimentosClienteNaoExiste() {
        // Primeiro criar um cliente e investimento para garantir que o endpoint funciona
        Long clienteId = criarClienteTeste();
        Long produtoId = criarProdutoTeste(0);
        
        // Criar um investimento para o cliente
        String json = """
                {
                  "clienteId": %d,
                  "produtoId": %d,
                  "valor": 1000.00,
                  "prazoMeses": 6,
                  "data": "2025-01-15"
                }
                """.formatted(clienteId, produtoId);
                
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when().post("/investimentos")
                .then()
                .statusCode(201);
        
        // Agora testar busca por cliente que não existe - deve retornar 200 com lista vazia
        given()
                .when().get("/investimentos/99999")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    public void testBuscarInvestimentosClienteSemInvestimentos() {
        Long clienteId = criarClienteTeste();

        given()
                .when().get("/investimentos/" + clienteId)
                .then()
                .statusCode(204); // NO_CONTENT quando lista vazia
    }

    private Long criarClienteTeste() {
        String clienteJson = """
                {
                  "nome": "Joao Teste",
                  "cpf": "12345678901",
                  "username": "joao.test",
                  "password": "senhaSegura",
                  "role": "cliente"
                }
                """;
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(clienteJson)
                .when().post("/clientes")
                .then()
                .statusCode(201)
                .extract().path("id");
        return id.longValue();
    }

    private Long criarProdutoTeste(int minimoDias) {
        String produtoJson = """
                {
                  "nome": "CDB Premium",
                  "tipo": "CDB",
                  "tipo_rentabilidade": "POS",
                  "rentabilidade": 1.2,
                  "periodo_rentabilidade": "AO_ANO",
                  "indice": "CDI",
                  "liquidez": 30,
                  "minimo_dias_investimento": %d,
                  "fgc": true
                }
                """.formatted(minimoDias);
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(produtoJson)
                .when().post("/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");
        return id.longValue();
    }
}

