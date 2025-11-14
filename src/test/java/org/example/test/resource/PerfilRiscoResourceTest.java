package org.example.test.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes para o PerfilRiscoResource
 */
@QuarkusTest
public class PerfilRiscoResourceTest {

    @BeforeEach
    public void limparDados() {
        // Limpar dados existentes
        given().when().delete("/produtos").then().statusCode(anyOf(is(204), is(404)));
        given().when().delete("/clientes").then().statusCode(anyOf(is(204), is(404)));
    }

    @Test
    public void testClienteNaoEncontrado() {
        given()
                .when().get("/perfil-risco/999999")
                .then()
                .statusCode(404)
                .body("message", containsString("Cliente não encontrado"));
    }

    @Test
    public void testClienteSemHistorico() {
        // Primeiro criar um cliente
        Long clienteId = criarClienteTeste();

        // Tentar buscar perfil sem histórico
        given()
                .when().get("/perfil-risco/" + clienteId)
                .then()
                .statusCode(400)
                .body("message", containsString("não possui histórico"));
    }

    @Test
    public void testPerfilConservadorComInvestimentos() {
        // Criar cliente, produtos e investimentos
        Long clienteId = criarClienteTeste();
        Long produtoSeguroId = criarProdutoSeguro(); // CDB com FGC

        // Criar investimento conservador
        criarInvestimento(clienteId, produtoSeguroId, 5000.0);

        // Verificar perfil
        given()
                .when().get("/perfil-risco/" + clienteId)
                .then()
                .statusCode(200)
                .body("clienteId", is(clienteId.intValue()))
                .body("perfil", is("Conservador"))
                .body("pontuacao", allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(33)))
                .body("descricao", containsString("segurança"));
    }

    @Test
    public void testPerfilModeradoComSimulacoes() {
        // Criar cliente e produtos
        Long clienteId = criarClienteTeste();
        criarProdutoSeguro();
        criarProdutoMedio();

        // Fazer simulações mistas
        fazerSimulacao(clienteId, 5000.0, "CDB");
        fazerSimulacao(clienteId, 3000.0, "TESOURO_DIRETO");

        // Verificar perfil
        given()
                .when().get("/perfil-risco/" + clienteId)
                .then()
                .statusCode(200)
                .body("clienteId", is(clienteId.intValue()))
                .body("perfil", is("Moderado"))
                .body("pontuacao", allOf(greaterThanOrEqualTo(34), lessThanOrEqualTo(66)))
                .body("descricao", containsString("equilibrado"));
    }

    // Métodos auxiliares
    private Long criarClienteTeste() {
        String clienteJson = """
                {
                    "nome": "Cliente Teste Perfil",
                    "cpf": "12345678901",
                    "username": "cliente.perfil.teste",
                    "password": "senha123",
                    "role": "cliente"
                }
                """;

        Integer clienteId = given()
                .contentType(ContentType.JSON)
                .body(clienteJson)
                .when().post("/clientes")
                .then()
                .statusCode(201)
                .extract().path("id");

        return clienteId.longValue();
    }

    private Long criarProdutoSeguro() {
        String produtoJson = """
                {
                    "nome": "CDB Seguro Teste",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.05,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "CDI",
                    "liquidez": 0,
                    "minimo_dias_investimento": 30,
                    "fgc": true
                }
                """;

        Integer produtoId = given()
                .contentType(ContentType.JSON)
                .body(produtoJson)
                .when().post("/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");

        return produtoId.longValue();
    }

    private Long criarProdutoMedio() {
        String produtoJson = """
                {
                    "nome": "Tesouro IPCA+ 2030",
                    "tipo": "TESOURO_DIRETO",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.02,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "IPCA",
                    "liquidez": 1,
                    "minimo_dias_investimento": 0,
                    "fgc": false
                }
                """;

        Integer produtoId = given()
                .contentType(ContentType.JSON)
                .body(produtoJson)
                .when().post("/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");

        return produtoId.longValue();
    }

    private void criarInvestimento(Long clienteId, Long produtoId, Double valor) {
        String investimentoJson = """
                {
                    "clienteId": %d,
                    "produtoId": %d,
                    "valor": %.2f,
                    "prazoMeses": 12,
                    "data": "2025-01-15"
                }
                """.formatted(clienteId, produtoId, valor);

        given()
                .contentType(ContentType.JSON)
                .body(investimentoJson)
                .when().post("/investimentos")
                .then()
                .statusCode(201);
    }

    private void fazerSimulacao(Long clienteId, Double valor, String tipoProduto) {
        String simulacaoJson = """
                {
                    "clienteId": %d,
                    "valor": %.2f,
                    "prazoMeses": 12,
                    "tipoProduto": "%s"
                }
                """.formatted(clienteId, valor, tipoProduto);

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when().post("/simular-investimento")
                .then()
                .statusCode(200);
    }
}