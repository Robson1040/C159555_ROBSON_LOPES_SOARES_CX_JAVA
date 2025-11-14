package org.example.test.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("Testes de Validação do SimulacaoInvestimentoResource")
public class SimulacaoValidationTest {

    @BeforeEach
    public void limparDados() {
        // Limpa produtos antes de cada teste
        given()
                .when().delete("/produtos")
                .then()
                .statusCode(anyOf(is(204), is(404)));
        
        // Cria um produto básico para os testes
        criarProdutoTeste();
    }

    @Test
    @DisplayName("Deve rejeitar simulação sem clienteId")
    public void testValidacaoClienteIdObrigatorio() {
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
                .body("errors", hasItem(containsString("ID do cliente é obrigatório")));
    }

    @Test
    @DisplayName("Deve rejeitar simulação sem valor")
    public void testValidacaoValorObrigatorio() {
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
                .body("errors", hasItem(containsString("Valor do investimento é obrigatório")));
    }

    @Test
    @DisplayName("Deve rejeitar valor menor que mínimo")
    public void testValidacaoValorMinimo() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 0.50,
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
                .body("errors", hasItem(containsString("Valor mínimo de investimento é R$ 1,00")));
    }

    @Test
    @DisplayName("Deve rejeitar valor maior que máximo")
    public void testValidacaoValorMaximo() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000000000.00,
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
                .body("errors", hasItem(containsString("Valor máximo de investimento é R$ 999.999.999,99")));
    }

    @Test
    @DisplayName("Deve rejeitar simulação sem nenhum prazo")
    public void testValidacaoSemPrazo() {
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
                .body("errors", hasItem(containsString("Informe pelo menos um prazo")));
    }

    @Test
    @DisplayName("Deve rejeitar múltiplos prazos informados")
    public void testValidacaoMultiplosPrazos() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoMeses": 12,
                    "prazoDias": 360
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
                .body("errors", hasItem(containsString("Informe apenas UM tipo de prazo")));
    }

    @Test
    @DisplayName("Deve rejeitar prazoMeses inválido")
    public void testValidacaoPrazoMesesInvalido() {
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
                .body("errors", hasItem(containsString("Prazo em meses deve ser no mínimo 1")));
    }

    @Test
    @DisplayName("Deve rejeitar prazoMeses muito alto")
    public void testValidacaoPrazoMesesMuitoAlto() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoMeses": 700
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
                .body("errors", hasItem(containsString("Prazo em meses deve ser no máximo 600")));
    }

    @Test
    @DisplayName("Deve rejeitar liquidez negativa")
    public void testValidacaoLiquidezNegativa() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoMeses": 12,
                    "liquidez": -10
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
                .body("errors", hasItem(containsString("Liquidez deve ser 0")));
    }

    @Test
    @DisplayName("Deve rejeitar inconsistência entre tipo rentabilidade PRE e índice")
    public void testValidacaoInconsistenciaPreComIndice() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoMeses": 12,
                    "tipo_rentabilidade": "PRE",
                    "indice": "CDI"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Produtos pré-fixados não devem ter índice específico"));
    }

    @Test
    @DisplayName("Deve rejeitar inconsistência entre tipo rentabilidade POS sem índice")
    public void testValidacaoInconsistenciaPosSemIndice() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoMeses": 12,
                    "tipo_rentabilidade": "POS",
                    "indice": "NENHUM"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Produtos pós-fixados devem ter um índice específico"));
    }

    @Test
    @DisplayName("Deve rejeitar ID inválido nos endpoints de consulta")
    public void testValidacaoIdNegativo() {
        given()
                .when()
                .get("/simular-investimento/-1")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"));

        given()
                .when()
                .get("/simular-investimento/historico/-1")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"));

        given()
                .when()
                .get("/simular-investimento/estatisticas/-1")
                .then()
                .statusCode(400)
                .body("message", containsString("Dados inválidos"));
    }

    @Test
    @DisplayName("Deve aceitar simulação válida")
    public void testSimulacaoValida() {
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 1000.00,
                    "prazoMeses": 12,
                    "tipoProduto": "CDB",
                    "tipo_rentabilidade": "POS",
                    "indice": "CDI"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201) // CREATED
                .body("clienteId", is(12345))
                .body("simulacaoId", notNullValue())
                .body("resultadoSimulacao", notNullValue())
                .body("resultadoSimulacao.valorSimulado", is(true));
    }

    private void criarProdutoTeste() {
        String cdbJson = """
                {
                    "nome": "CDB Teste Validação",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.05,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "CDI",
                    "liquidez": 30,
                    "minimo_dias_investimento": 1,
                    "fgc": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(cdbJson)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201);
    }
}