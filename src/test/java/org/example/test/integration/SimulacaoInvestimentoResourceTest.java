package org.example.test.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class SimulacaoInvestimentoResourceTest {
        // Método limparDados removido conforme solicitação; testes utilizam estado compartilhado.

    @Test
    public void testSimulacaoComFiltroCompleto() {
        // Primeiro, cria alguns produtos de teste
        criarProdutoTeste();

        String simulacaoJson = """
                {
                    "clienteId": 123456,
                    "valor": 10000.00,
                    "prazoMeses": 12,
                    "tipoProduto": "CDB",
                    "tipo_rentabilidade": "POS",
                    "indice": "CDI",
                    "fgc": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200)
                .body("clienteId", is(123456))
                .body("produtoValidado", notNullValue())
                .body("produtoValidado.tipo", is("CDB"))
                .body("resultadoSimulacao", notNullValue())
                .body("resultadoSimulacao.valorFinal", greaterThan(10000.0f))
                .body("resultadoSimulacao.prazoMeses", is(12))
                .body("dataSimulacao", notNullValue());
    }

    @Test
    public void testSimulacaoSemFiltros() {
        // Cria produtos de teste
        criarProdutoTeste();

        String simulacaoJson = """
                {
                    "clienteId": 789012,
                    "valor": 5000.00,
                    "prazoAnos": 2
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200)
                .body("clienteId", is(789012))
                .body("produtoValidado", notNullValue())
                .body("resultadoSimulacao.valorFinal", greaterThan(5000.0f))
                .body("resultadoSimulacao.prazoAnos", is(2));
    }

    @Test
    public void testSimulacaoComPrazoDias() {
        criarProdutoTeste();

        String simulacaoJson = """
                {
                    "clienteId": 345678,
                    "valor": 15000.00,
                    "prazoDias": 180,
                    "tipoProduto": "TESOURO_DIRETO"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200)
                .body("clienteId", is(345678))
                .body("produtoValidado.tipo", is("TESOURO_DIRETO"))
                .body("resultadoSimulacao.prazoDias", is(180));
    }

    @Test
    public void testSimulacaoComDadosInvalidos() {
        String simulacaoJson = """
                {
                    "clienteId": null,
                    "valor": -1000.00,
                    "prazoMeses": 6
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

    @Test
    public void testSimulacaoSemProdutoDisponivel() {
        // Não cria produtos, então não deve encontrar nenhum

        String simulacaoJson = """
                {
                    "clienteId": 111222,
                    "valor": 1000.00,
                    "prazoMeses": 3,
                    "tipoProduto": "FUNDO",
                    "tipo_rentabilidade": "POS"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400)
                .body("message", containsString("Nenhum produto encontrado"));
    }

    /**
     * Helper para criar produtos de teste
     */
    private void criarProdutoTeste() {
        // CDB com FGC
        String cdbJson = """
                {
                    "nome": "CDB Banco XYZ",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.05,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "CDI",
                    "liquidez": 90,
                    "minimo_dias_investimento": 30,
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

        // Tesouro Direto
        String tesouroJson = """
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

        given()
                .contentType(ContentType.JSON)
                .body(tesouroJson)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201);

        // LCI Pré-fixada
        String lciJson = """
                {
                    "nome": "LCI Pré-fixada",
                    "tipo": "LCI",
                    "tipo_rentabilidade": "PRE",
                    "rentabilidade": 9.5,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "NENHUM",
                    "liquidez": 180,
                    "minimo_dias_investimento": 90,
                    "fgc": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(lciJson)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201);
    }
}