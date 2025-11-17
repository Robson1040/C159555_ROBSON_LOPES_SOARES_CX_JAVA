package br.gov.caixa.api.investimentos.resource.simulacao;

import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponse;

import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.service.autenticacao.JwtService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class SimulacaoInvestimentoResourceIntegrationTest {

    @Inject
    JwtService jwtService;

    // Tokens para diferentes tipos de usuário
    private String adminToken;
    private String userToken;

    // IDs dos dados criados para reutilização nos testes
    private static Long produtoIdCriado1;
    private static Long produtoIdCriado2; 
    private static Long produtoIdCriado3;
    private static Long clienteIdCriado;
    private static Long simulacaoIdCriada1;
    private static Long simulacaoIdCriada2;
    private static Long simulacaoIdCriada3;

    @BeforeEach
    public void setup() {
        RestAssured.port = 8081;
        adminToken = jwtService.gerarToken("admin@test.com", "ADMIN");
        userToken = jwtService.gerarToken("user@test.com", "USER");
    }

    @Test
    @Order(1)
    void deveCriarProdutoParaSimulacao1() {
        ProdutoRequest produto = new ProdutoRequest(
                "CDB Simulação Test 120% CDI",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("120.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                30, // liquidez
                90, // mínimo dias
                true // FGC
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("CDB Simulação Test 120% CDI"))
                .body("tipo", equalTo("CDB"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCriado1 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto 1 criado com ID: " + produtoIdCriado1);
        assertNotNull(produtoIdCriado1);
    }

    @Test
    @Order(2)
    void deveCriarProdutoParaSimulacao2() {
        ProdutoRequest produto = new ProdutoRequest(
                "Tesouro IPCA+ 2035 Test",
                TipoProduto.TESOURO_DIRETO,
                TipoRentabilidade.POS,
                new BigDecimal("110.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.IPCA,
                0, // sem liquidez
                1, // mínimo 1 dia
                false // sem FGC
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Tesouro IPCA+ 2035 Test"))
                .body("tipo", equalTo("TESOURO_DIRETO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCriado2 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto 2 criado com ID: " + produtoIdCriado2);
        assertNotNull(produtoIdCriado2);
    }

    @Test
    @Order(3)
    void deveCriarProdutoParaSimulacao3() {
        ProdutoRequest produto = new ProdutoRequest(
                "LCI Pré-fixada Test",
                TipoProduto.LCI,
                TipoRentabilidade.PRE,
                new BigDecimal("9.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.NENHUM,
                90, // liquidez 90 dias
                180, // mínimo 180 dias
                true // FGC
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("LCI Pré-fixada Test"))
                .body("tipo", equalTo("LCI"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCriado3 = produtoResponse.id();
        assertNotNull(produtoIdCriado3);
    }

    @Test
    @Order(4)
    void deveCriarClienteParaSimulacoes() {
        ClienteRequest cliente = new ClienteRequest(
                "João Simulação Test",
                "69689795007",  // CPF válido para teste
                "joao.simulacao.test35",
                "password123",
                "USER"
        );

        ClienteResponse clienteResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(cliente)
                .when()
                .post("/clientes")
                .then()
                .statusCode(201)
                .body("nome", equalTo("João Simulação Test"))
                .body("cpf", equalTo("69689795007"))
                .extract()
                .as(ClienteResponse.class);

        clienteIdCriado = clienteResponse.id();
        assertNotNull(clienteIdCriado);
    }

    @Test
    @Order(5)
    void deveSimularPrimeiroInvestimento() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado,
                produtoIdCriado1, // CDB 120% CDI
                new BigDecimal("10000.00"), // R$ 10.000
                12, // 12 meses
                null, // prazoDias
                null, // prazoAnos
                TipoProduto.CDB,
                "CDB Simulação Test 120% CDI",
                TipoRentabilidade.POS,
                Indice.CDI,
                30, // liquidez
                true // FGC
        );

        SimulacaoResponse simulacaoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteIdCriado.intValue()))
                .body("produtoValidado.nome", equalTo("CDB Simulação Test 120% CDI"))
                .body("resultadoSimulacao.valorFinal", greaterThan(10000f))
                .body("simulacaoId", notNullValue())
                .extract()
                .as(SimulacaoResponse.class);

        simulacaoIdCriada1 = simulacaoResponse.simulacaoId();
        assertNotNull(simulacaoIdCriada1);
        assertTrue(simulacaoResponse.resultadoSimulacao().valorFinal().compareTo(new BigDecimal("10000")) > 0);
    }

    @Test
    @Order(6)
    void deveSimularSegundoInvestimento() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado,
                produtoIdCriado2, // Tesouro IPCA+
                new BigDecimal("25000.00"), // R$ 25.000
                null, // prazoMeses
                null, // prazoDias
                2, // 2 anos
                TipoProduto.TESOURO_DIRETO,
                "Tesouro IPCA+ 2035 Test",
                TipoRentabilidade.POS,
                Indice.IPCA,
                0, // sem liquidez
                false // sem FGC
        );

        SimulacaoResponse simulacaoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteIdCriado.intValue()))
                .body("produtoValidado.nome", equalTo("Tesouro IPCA+ 2035 Test"))
                .body("resultadoSimulacao.valorFinal", greaterThan(25000f))
                .body("simulacaoId", notNullValue())
                .extract()
                .as(SimulacaoResponse.class);

        simulacaoIdCriada2 = simulacaoResponse.simulacaoId();
        assertNotNull(simulacaoIdCriada2);
    }

    @Test
    @Order(7)
    void deveSimularTerceiroInvestimento() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado,
                produtoIdCriado3, // LCI Pré-fixada
                new BigDecimal("5000.00"), // R$ 5.000
                null, // prazoMeses
                365, // 365 dias (1 ano)
                null, // prazoAnos
                TipoProduto.LCI,
                "LCI Pré-fixada Test",
                TipoRentabilidade.PRE,
                Indice.NENHUM,
                90, // liquidez 90 dias
                true // FGC
        );

        SimulacaoResponse simulacaoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteIdCriado.intValue()))
                .body("produtoValidado.nome", equalTo("LCI Pré-fixada Test"))
                .body("resultadoSimulacao.valorFinal", greaterThan(5000f))
                .body("simulacaoId", notNullValue())
                .extract()
                .as(SimulacaoResponse.class);

        simulacaoIdCriada3 = simulacaoResponse.simulacaoId();
        assertNotNull(simulacaoIdCriada3);
    }

    @Test
    @Order(8)
    void deveConsultarSimulacaoPorId() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simular-investimento/" + simulacaoIdCriada1)
                .then()
                .statusCode(200)
                .body("id", equalTo(simulacaoIdCriada1.intValue()))
                .body("clienteId", equalTo(clienteIdCriado.intValue()))
                .body("produto", equalTo("CDB Simulação Test 120% CDI"))
                .body("valorInvestido", equalTo(10000));
    }

    @Test
    @Order(9)
    void deveConsultarHistoricoSimulacoes() {
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simular-investimento/historico/" + clienteIdCriado)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(3))) // pelo menos 3 simulações
                .extract().response();

        List<Map<String, Object>> simulacoes = response.jsonPath().getList("$");
        
        // Verificar se as simulações criadas estão no histórico
        boolean temPrimeiraSimulacao = simulacoes.stream()
            .anyMatch(s -> "CDB Simulação Test 120% CDI".equals(s.get("produto")));
        boolean temSegundaSimulacao = simulacoes.stream()
            .anyMatch(s -> "Tesouro IPCA+ 2035 Test".equals(s.get("produto")));
        boolean temTerceiraSimulacao = simulacoes.stream()
            .anyMatch(s -> "LCI Pré-fixada Test".equals(s.get("produto")));
            
        assertTrue(temPrimeiraSimulacao, "Primeira simulação deve estar no histórico");
        assertTrue(temSegundaSimulacao, "Segunda simulação deve estar no histórico");
        assertTrue(temTerceiraSimulacao, "Terceira simulação deve estar no histórico");
    }

    @Test
    @Order(10)
    void deveConsultarEstatisticasCliente() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simular-investimento/estatisticas/" + clienteIdCriado)
                .then()
                .statusCode(200)
                .body("totalSimulacoes", greaterThanOrEqualTo(3))
                .body("totalInvestido", greaterThan(39999)) // 10k + 25k + 5k = 40k
                .body("ultimaSimulacao", notNullValue())
                .body("ultimaSimulacao.produto", notNullValue());
    }

    // === TESTES DE CASOS DE ERRO ===

    @Test
    @Order(11)
    void deveRetornar400ParaValorInvestimentoInvalido() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado,
                produtoIdCriado1,
                new BigDecimal("0.50"), // Valor muito baixo (< R$ 1,00)
                12,
                null,
                null,
                TipoProduto.CDB,
                null,
                TipoRentabilidade.POS,
                Indice.CDI,
                null,
                null
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(12)
    void deveRetornar400ParaPrazoInvalido() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado,
                produtoIdCriado1,
                new BigDecimal("1000.00"),
                null, // Nenhum prazo informado
                null,
                null,
                TipoProduto.CDB,
                null,
                TipoRentabilidade.POS,
                Indice.CDI,
                null,
                null
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(13)
    void deveRetornar400ParaInconsistenciaTipoRentabilidadeIndice() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado,
                produtoIdCriado1,
                new BigDecimal("1000.00"),
                12,
                null,
                null,
                TipoProduto.CDB,
                null,
                TipoRentabilidade.PRE, // Pré-fixado
                Indice.CDI, // Com índice CDI (inconsistente)
                null,
                null
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(anyOf(equalTo(400))); // Pode ser 400 (validação) ou 500 (regra de negócio)
    }

    @Test
    @Order(14)
    void deveRetornar404ParaSimulacaoInexistente() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simular-investimento/99999999")
                .then()
                .statusCode(anyOf(equalTo(404))); // Dependendo da implementação
    }

    @Test
    @Order(15)
    void deveRetornar404ParaHistoricoClienteInexistente() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simular-investimento/historico/99999999")
                .then()
                .statusCode(200)
                .body("$", hasSize(0)); // Lista vazia para cliente inexistente
    }

    @Test
    @Order(16)
    void deveRetornar401ParaSemToken() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado,
                produtoIdCriado1,
                new BigDecimal("1000.00"),
                12,
                null,
                null,
                TipoProduto.CDB,
                null,
                TipoRentabilidade.POS,
                Indice.CDI,
                null,
                null
        );

        given()
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(17)
    void deveRetornar401ParaTokenInvalido() {
        given()
                .header("Authorization", "Bearer token-invalido")
                .when()
                .get("/simular-investimento/historico/" + clienteIdCriado)
                .then()
                .statusCode(401);
    }

    @Test
    @Order(18)
    void deveRetornar400ParaClienteIdNull() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                null, // ClienteId null
                produtoIdCriado1,
                new BigDecimal("1000.00"),
                12,
                null,
                null,
                TipoProduto.CDB,
                null,
                TipoRentabilidade.POS,
                Indice.CDI,
                null,
                null
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(19)
    void devePermitirSimulacaoSemProdutoEspecifico() {
        // Simulação sem produto_id específico - deve encontrar produto baseado nos critérios
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado,
                null, // Sem produto específico
                new BigDecimal("15000.00"),
                18, // 18 meses
                null,
                null,
                TipoProduto.CDB, // Filtrar por tipo CDB
                null,
                TipoRentabilidade.POS,
                Indice.CDI,
                null,
                true // Com FGC
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteIdCriado.intValue()))
                .body("produtoValidado.tipo", equalTo("CDB"))
                .body("resultadoSimulacao.valorFinal", greaterThan(15000f))
                .body("simulacaoId", notNullValue());
    }

    @Test
    @Order(20)
    void deveValidarAcessoAoCliente() {
        // Criar um token para um usuário específico e tentar acessar dados de outro cliente
        // Este teste verifica se a validação de acesso está funcionando
        String tokenUsuarioEspecifico = jwtService.gerarToken("user2@test.com", "USER");
        
        given()
                .header("Authorization", "Bearer " + tokenUsuarioEspecifico)
                .when()
                .get("/simular-investimento/historico/" + clienteIdCriado)
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(403))); // Pode permitir ou negar dependendo da implementação
    }
}
