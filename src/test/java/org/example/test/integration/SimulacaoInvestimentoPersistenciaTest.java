package org.example.test.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.example.model.SimulacaoInvestimento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimulacaoInvestimentoPersistenciaTest {

    @BeforeEach
    public void limparDados() {
        // Limpa produtos e simulações antes de cada teste
        given()
                .when().delete("/produtos")
                .then()
                .statusCode(anyOf(is(204), is(404)));
        
        // Limpa simulações
        SimulacaoInvestimento.deleteAll();
    }

    @Test
    @Order(1)
    public void testSimulacaoEhPersistidaNoBanco() {
        // 1. Criar um produto para a simulação
        criarProdutoTeste();

        // 2. Fazer uma simulação
        String simulacaoJson = """
                {
                    "clienteId": 12345,
                    "valor": 5000.00,
                    "prazoMeses": 12,
                    "tipoProduto": "CDB"
                }
                """;

        Long simulacaoId = given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200)
                .body("clienteId", is(12345))
                .body("simulacaoId", notNullValue())
                .body("resultadoSimulacao.valorSimulado", is(true))
                .extract()
                .jsonPath()
                .getLong("simulacaoId");

        // 3. Verificar se foi persistida no banco
        assertNotNull(simulacaoId);
        assertTrue(simulacaoId > 0);

        // 4. Buscar a simulação persistida
        SimulacaoInvestimento simulacao = SimulacaoInvestimento.findById(simulacaoId);
        assertNotNull(simulacao);
        assertEquals(12345L, simulacao.clienteId);
        assertEquals(5000.00, simulacao.valorInvestido.doubleValue(), 0.01);
        assertEquals(12, simulacao.prazoMeses);
        assertTrue(simulacao.valorFinal.doubleValue() > 5000.00);
        assertNotNull(simulacao.dataSimulacao);
        assertTrue(simulacao.valorSimulado);
        assertNotNull(simulacao.cenarioSimulacao);
        
        System.out.println("Simulação persistida: " + simulacao);
    }

    @Test
    @Order(2)
    public void testBuscarHistoricoSimulacoes() {
        // 1. Criar produto
        criarProdutoTeste();

        // 2. Fazer múltiplas simulações para o mesmo cliente
        Long clienteId = 67890L;
        
        // Primeira simulação
        String simulacao1 = """
                {
                    "clienteId": %d,
                    "valor": 1000.00,
                    "prazoMeses": 6
                }
                """.formatted(clienteId);

        given()
                .contentType(ContentType.JSON)
                .body(simulacao1)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200);

        // Segunda simulação
        String simulacao2 = """
                {
                    "clienteId": %d,
                    "valor": 2000.00,
                    "prazoMeses": 12
                }
                """.formatted(clienteId);

        given()
                .contentType(ContentType.JSON)
                .body(simulacao2)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200);

        // 3. Buscar histórico via API
        given()
                .when()
                .get("/simular-investimento/historico/" + clienteId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].clienteId", is(clienteId.intValue()))
                .body("[1].clienteId", is(clienteId.intValue()));

        // 4. Verificar via entidade
        var simulacoes = SimulacaoInvestimento.findByClienteId(clienteId);
        assertEquals(2, simulacoes.size());
        
        // Verificar que são ordenadas por data (mais recente primeiro)
        var simulacoesOrdenadas = SimulacaoInvestimento.findByClienteIdOrderByDate(clienteId);
        assertEquals(2, simulacoesOrdenadas.size());
        assertTrue(simulacoesOrdenadas.get(0).dataSimulacao.isAfter(
                simulacoesOrdenadas.get(1).dataSimulacao) ||
                simulacoesOrdenadas.get(0).dataSimulacao.isEqual(
                simulacoesOrdenadas.get(1).dataSimulacao));
    }

    @Test
    @Order(3)
    public void testEstatisticasCliente() {
        // 1. Criar produto
        criarProdutoTeste();

        Long clienteId = 11111L;

        // 2. Fazer 3 simulações com valores diferentes
        double[] valores = {1000.00, 2000.00, 3000.00};
        
        for (double valor : valores) {
            String simulacaoJson = """
                    {
                        "clienteId": %d,
                        "valor": %.2f,
                        "prazoMeses": 12
                    }
                    """.formatted(clienteId, valor);

            given()
                    .contentType(ContentType.JSON)
                    .body(simulacaoJson)
                    .when()
                    .post("/simular-investimento")
                    .then()
                    .statusCode(200);
        }

        // 3. Buscar estatísticas via API
        given()
                .when()
                .get("/simular-investimento/estatisticas/" + clienteId)
                .then()
                .statusCode(200)
                .body("totalSimulacoes", is(3))
                .body("totalInvestido", is(6000.0f))
                .body("mediaValorInvestido", is(2000.0f))
                .body("ultimaSimulacao", notNullValue())
                .body("ultimaSimulacao.clienteId", is(clienteId.intValue()));

        // 4. Verificar via service
        long count = SimulacaoInvestimento.countByClienteId(clienteId);
        assertEquals(3, count);
    }

    @Test
    @Order(4) 
    public void testBuscarSimulacaoPorId() {
        // 1. Criar produto
        criarProdutoTeste();

        // 2. Fazer uma simulação e capturar ID
        String simulacaoJson = """
                {
                    "clienteId": 99999,
                    "valor": 15000.00,
                    "prazoMeses": 24
                }
                """;

        Long simulacaoId = given()
                .contentType(ContentType.JSON)
                .body(simulacaoJson)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("simulacaoId");

        // 3. Buscar por ID via API
        given()
                .when()
                .get("/simular-investimento/" + simulacaoId)
                .then()
                .statusCode(200)
                .body("id", is(simulacaoId.intValue()))
                .body("clienteId", is(99999))
                .body("valorInvestido", is(15000.0f))
                .body("prazoMeses", is(24));

        // 4. Testar ID inexistente
        given()
                .when()
                .get("/simular-investimento/999999")
                .then()
                .statusCode(404)
                .body("message", is("Simulação não encontrada"));
    }

    private void criarProdutoTeste() {
        String cdbJson = """
                {
                    "nome": "CDB Teste Persistencia",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.10,
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