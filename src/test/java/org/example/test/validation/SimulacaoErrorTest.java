package org.example.test.validation;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Teste específico para validar os casos de erro 400 na simulação
 */
@QuarkusTest
@DisplayName("Testes de Status Code 400 para Simulação de Investimento")
public class SimulacaoErrorTest {

    @Test
    @DisplayName("Valor 0 reais deve retornar 400")
    public void testValorZeroRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 0.00,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("timestamp", notNullValue())
                .body("errors", hasItem(containsString("Valor mínimo de investimento é R$ 1,00")));
    }

    @Test
    @DisplayName("Valor negativo deve retornar 400")
    public void testValorNegativoRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": -1000.00,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("errors", hasItem(containsString("Valor mínimo de investimento é R$ 1,00")));
    }

    @Test
    @DisplayName("Prazo 0 meses deve retornar 400")
    public void testPrazoZeroMesesRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoMeses": 0
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("errors", hasItem(containsString("Prazo em meses deve ser no mínimo 1")));
    }

    @Test
    @DisplayName("Prazo 0 dias deve retornar 400")
    public void testPrazoZeroDiasRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoDias": 0
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("errors", hasItem(containsString("Prazo em dias deve ser no mínimo 1")));
    }

    @Test
    @DisplayName("Prazo 0 anos deve retornar 400")
    public void testPrazoZeroAnosRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoAnos": 0
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("errors", hasItem(containsString("Prazo em anos deve ser no mínimo 1")));
    }

    @Test
    @DisplayName("Cliente ID null deve retornar 400")
    public void testClienteIdNullRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": null,
                    "valor": 1000.00,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("errors", hasItem(containsString("ID do cliente é obrigatório")));
    }

    @Test
    @DisplayName("Sem campo clienteId deve retornar 400")
    public void testSemClienteIdRetorna400() {
        String simulacaoJson = """
                {
                    "valor": 1000.00,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("errors", hasItem(containsString("ID do cliente é obrigatório")));
    }

    @Test
    @DisplayName("Sem campo valor deve retornar 400")
    public void testSemValorRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("errors", hasItem(containsString("Valor do investimento é obrigatório")));
    }

    @Test
    @DisplayName("Sem campo de prazo deve retornar 400 (validação customizada)")
    public void testSemPrazoRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"))
                .body("status", is(400))
                .body("errors", hasItem(containsString("Informe pelo menos um prazo")));
    }

    @Test
    @DisplayName("JSON malformado deve retornar 400")
    public void testJsonMalformadoRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoMeses": 12,
                """; // JSON incompleto

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Campo com tipo errado deve retornar 400")
    public void testTipoErradoRetorna400() {
        String simulacaoJson = """
                {
                    "clienteId": "abc",
                    "valor": 1000.00,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400);
    }
}