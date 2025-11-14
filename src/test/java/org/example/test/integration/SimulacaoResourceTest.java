package org.example.test.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes para o endpoint GET /simulacoes
 */
@QuarkusTest
@DisplayName("Testes do SimulacaoResource - GET /simulacoes")
public class SimulacaoResourceTest {

    @BeforeEach
    public void setup() {
        // Cria um produto para permitir simulações se não existe
        criarProdutoTeste();
    }

    @Test
    @DisplayName("GET /simulacoes deve retornar lista vazia inicialmente")
    public void testListarSimulacoesVazia() {
        given()
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /simulacoes deve retornar simulações após criação")
    public void testListarSimulacoesComDados() {
        // Primeiro, cria uma simulação
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 10000.00,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201);

        // Agora verifica se a simulação aparece na listagem
        given()
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", notNullValue())
                .body("[0].clienteId", is(12345))
                .body("[0].produto", notNullValue())
                .body("[0].valorInvestido", is(10000.00f))
                .body("[0].valorFinal", greaterThan(10000.00f))
                .body("[0].prazoMeses", is(12))
                .body("[0].dataSimulacao", notNullValue());
    }

    @Test
    @DisplayName("GET /simulacoes deve retornar múltiplas simulações")
    public void testListarMultiplasSimulacoes() {
        // Cria primeira simulação
        String simulacao1Json = """
                {
                    "clienteId": 11111,
                    "valor": 5000.00,
                    "prazoMeses": 6
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacao1Json)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201);

        // Cria segunda simulação
        String simulacao2Json = """
                {
                    "clienteId": 22222,
                    "valor": 15000.00,
                    "prazoDias": 180
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacao2Json)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201);

        // Verifica se ambas aparecem na listagem
        given()
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("clienteId", hasItems(11111, 22222));
    }

    @Test
    @DisplayName("GET /simulacoes deve incluir diferentes tipos de prazo")
    public void testListarSimulacoesComDiferentesPrazos() {
        // Simulação com prazoAnos
        String simulacaoAnos = """
                {
                    "clienteId": 33333,
                    "valor": 20000.00,
                    "prazoAnos": 2
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoAnos)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201);

        // Verifica se o prazoAnos aparece corretamente
        given()
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].clienteId", is(33333))
                .body("[0].prazoAnos", is(2))
                .body("[0].prazoMeses", nullValue())
                .body("[0].prazoDias", nullValue());
    }

    @Test
    @DisplayName("GET /simulacoes/por-produto-dia deve retornar lista vazia inicialmente")
    public void testAgrupamentoPorProdutoDiaVazio() {
        given()
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /simulacoes/por-produto-dia deve agrupar simulações por produto e data")
    public void testAgrupamentoPorProdutoEDia() {
        // Cria duas simulações do mesmo produto no mesmo dia
        String simulacao1Json = """
                {
                    "clienteId": 11111,
                    "valor": 10000.00,
                    "prazoMeses": 12
                }
                """;

        String simulacao2Json = """
                {
                    "clienteId": 22222,
                    "valor": 20000.00,
                    "prazoMeses": 12
                }
                """;

        // Executa as simulações
        given()
                .contentType(ContentType.JSON)
                .body(simulacao1Json)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(simulacao2Json)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201);

        // Verifica o agrupamento
        given()
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].produto", is("CDB Teste"))
                .body("[0].quantidadeSimulacoes", is(2))
                .body("[0].mediaValorInvestido", is(15000.00f)) // (10000 + 20000) / 2
                .body("[0].mediaValorFinal", greaterThan(15000.00f))
                .body("[0].data", notNullValue());
    }

    @Test
    @DisplayName("GET /simulacoes/por-produto-dia deve agrupar produtos diferentes")
    public void testAgrupamentoProdutosDiferentes() {
        // Cria outro produto
        String produto2Json = """
                {
                    "nome": "Tesouro Direto Teste",
                    "tipo": "TESOURO_DIRETO",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 8.5,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "IPCA",
                    "liquidez": 1,
                    "minimo_dias_investimento": 0,
                    "fgc": false
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(produto2Json)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201);

        // Simulação com CDB
        String simulacaoCdb = """
                {
                    "clienteId": 11111,
                    "valor": 10000.00,
                    "prazoMeses": 12,
                    "tipoProduto": "CDB"
                }
                """;

        // Simulação com Tesouro
        String simulacaoTesouro = """
                {
                    "clienteId": 22222,
                    "valor": 5000.00,
                    "prazoMeses": 12,
                    "tipoProduto": "TESOURO_DIRETO"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoCdb)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(simulacaoTesouro)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201);

        // Verifica que há dois grupos de produto
        given()
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("produto", hasItems("CDB Teste", "Tesouro Direto Teste"))
                .body("quantidadeSimulacoes", everyItem(is(1)));
    }

    /**
     * Helper para criar produto de teste apenas se necessário
     */
    private void criarProdutoTeste() {
        // Verifica se já existe produto CDB Teste
        given()
                .when()
                .get("/produtos?nome=CDB Teste")
                .then()
                .statusCode(anyOf(is(200), is(204)));

        String produtoJson = """
                {
                    "nome": "CDB Teste",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "PRE",
                    "rentabilidade": 10.5,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "NENHUM",
                    "liquidez": 90,
                    "minimo_dias_investimento": 30,
                    "fgc": true
                }
                """;

        // Cria o produto (tolerando se já existir)
        given()
                .contentType(ContentType.JSON)
                .body(produtoJson)
                .when()
                .post("/produtos")
                .then()
                .statusCode(anyOf(is(201), is(409)));  // 201 = criado, 409 = conflict (já existe)
    }
}