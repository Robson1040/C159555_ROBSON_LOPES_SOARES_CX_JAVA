package org.example.test.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

/**
 * Testes para os novos endpoints de agrupamento de simulações
 */
@QuarkusTest
public class SimulacaoAgrupamentoResourceTest {

    @Test
    public void testEndpointPorProdutoMes() {
        given()
            .when().get("/simulacoes/por-produto-mes")
            .then()
            .statusCode(200)
            .contentType("application/json");
    }

    @Test
    public void testEndpointPorProdutoAno() {
        given()
            .when().get("/simulacoes/por-produto-ano")
            .then()
            .statusCode(200)
            .contentType("application/json");
    }

    @Test
    public void testEndpointPorProdutoDiaExistente() {
        // Verifica se o endpoint existente ainda funciona
        given()
            .when().get("/simulacoes/por-produto-dia")
            .then()
            .statusCode(200)
            .contentType("application/json");
    }
}