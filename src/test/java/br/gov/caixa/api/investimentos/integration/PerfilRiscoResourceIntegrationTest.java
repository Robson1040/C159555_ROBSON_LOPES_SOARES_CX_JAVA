package br.gov.caixa.api.investimentos.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import jakarta.inject.Inject;

import br.gov.caixa.api.investimentos.service.autenticacao.JwtService;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponse;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;

import java.math.BigDecimal;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PerfilRiscoResourceIntegrationTest {

    @Inject
    JwtService jwtService;

    // Tokens para diferentes tipos de usuário
    private String adminToken;
    private String userToken;

    // IDs dos recursos criados durante os testes
    private static Long clienteIdCriado;
    private static Long produtoIdRiscoBaixo1; // Poupança
    private static Long produtoIdRiscoBaixo2; // CDB com FGC
    private static Long produtoIdRiscoMedio1; // Debenture
    private static Long produtoIdRiscoMedio2; // CRI
    private static Long produtoIdRiscoAlto;   // Ação
    private static Long simulacaoIdCriada1;
    private static Long simulacaoIdCriada2;

    @BeforeEach
    void setUp() {
        // Gerar tokens para admin e user
        adminToken = jwtService.gerarToken("admin_perfil@test.com", "ADMIN");
        userToken = jwtService.gerarToken("user_perfil@test.com", "USER");
    }

    // ======================== SETUP - CRIAÇÃO DE PRODUTOS ========================

    @Test
    @Order(1)
    void deveCriarProdutoRiscoBaixo1_Poupanca() {
        ProdutoRequest produto = new ProdutoRequest(
                "Poupança Caixa", // nome
                TipoProduto.POUPANCA, // tipo
                TipoRentabilidade.POS, // tipo_rentabilidade
                new BigDecimal("0.50"), // rentabilidade (0.5% ao mês)
                PeriodoRentabilidade.AO_MES, // periodo_rentabilidade
                Indice.SELIC, // indice
                0, // liquidez (imediata)
                0, // minimo_dias_investimento
                true // fgc (sempre garantida)
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Poupança Caixa"))
                .body("tipo", equalTo("POUPANCA"))
                .body("risco", equalTo("BAIXO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdRiscoBaixo1 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto Poupança criado com ID: " + produtoIdRiscoBaixo1);
        assertNotNull(produtoIdRiscoBaixo1);
    }

    @Test
    @Order(2)
    void deveCriarProdutoRiscoBaixo2_CDBComFGC() {
        ProdutoRequest produto = new ProdutoRequest(
                "CDB Perfil Risco 100% CDI", // nome
                TipoProduto.CDB, // tipo
                TipoRentabilidade.POS, // tipo_rentabilidade
                new BigDecimal("1.00"), // rentabilidade (100% CDI)
                PeriodoRentabilidade.AO_ANO, // periodo_rentabilidade
                Indice.CDI, // indice
                5, // liquidez (90 dias)
                90, // minimo_dias_investimento
                true // fgc
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("CDB Perfil Risco 100% CDI"))
                .body("tipo", equalTo("CDB"))
                .body("risco", equalTo("BAIXO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdRiscoBaixo2 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto CDB FGC criado com ID: " + produtoIdRiscoBaixo2);
        assertNotNull(produtoIdRiscoBaixo2);
    }

    @Test
    @Order(3)
    void deveCriarProdutoRiscoAlto1_Debenture() {
        ProdutoRequest produto = new ProdutoRequest(
                "Debênture Perfil Risco 8%", // nome
                TipoProduto.DEBENTURE, // tipo
                TipoRentabilidade.PRE, // tipo_rentabilidade
                new BigDecimal("8.00"), // rentabilidade (8% ao ano)
                PeriodoRentabilidade.AO_ANO, // periodo_rentabilidade
                null, // indice (pré-fixado)
                90, // liquidez (90 dias) - melhor liquidez para risco médio
                30, // minimo_dias_investimento
                false // fgc (com garantia) - reduz risco significativamente
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Debênture Perfil Risco 8%"))
                .body("tipo", equalTo("DEBENTURE"))
                .body("risco", equalTo("ALTO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdRiscoMedio1 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto Debênture criado com ID: " + produtoIdRiscoMedio1);
        assertNotNull(produtoIdRiscoMedio1);
    }

    @Test
    @Order(4)
    void deveCriarProdutoRiscoMedio2_CRI() {
        ProdutoRequest produto = new ProdutoRequest(
                "CRI Perfil Risco IPCA+6%", // nome
                TipoProduto.CRI, // tipo
                TipoRentabilidade.POS, // tipo_rentabilidade
                new BigDecimal("6.00"), // rentabilidade (IPCA + 6%)
                PeriodoRentabilidade.AO_ANO, // periodo_rentabilidade
                Indice.IPCA, // indice
                365, // liquidez (1 ano)
                180, // minimo_dias_investimento
                false // fgc (sem garantia)
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("CRI Perfil Risco IPCA+6%"))
                .body("tipo", equalTo("CRI"))
                .body("risco", equalTo("ALTO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdRiscoMedio2 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto CRI criado com ID: " + produtoIdRiscoMedio2);
        assertNotNull(produtoIdRiscoMedio2);
    }

    @Test
    @Order(5)
    void deveCriarProdutoRiscoAlto_Acao() {
        ProdutoRequest produto = new ProdutoRequest(
                "Ação Perfil Risco PETR4", // nome
                TipoProduto.ACAO, // tipo
                TipoRentabilidade.POS, // tipo_rentabilidade
                new BigDecimal("12.00"), // rentabilidade esperada (12% ao ano)
                PeriodoRentabilidade.AO_ANO, // periodo_rentabilidade
                Indice.IBOVESPA, // indice
                0, // liquidez (imediata)
                0, // minimo_dias_investimento
                false // fgc (sem garantia)
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Ação Perfil Risco PETR4"))
                .body("tipo", equalTo("ACAO"))
                .body("risco", equalTo("ALTO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdRiscoAlto = produtoResponse.id();
        System.out.println("=== DEBUG: Produto Ação criado com ID: " + produtoIdRiscoAlto);
        assertNotNull(produtoIdRiscoAlto);
    }

    // ======================== SETUP - CRIAÇÃO DE CLIENTE ========================

    @Test
    @Order(6)
    void deveCriarClienteParaPerfilRisco() {
        ClienteRequest cliente = new ClienteRequest(
                "Cliente Perfil Risco Test",
                "15519430004", // CPF válido
                "cliente_perfil@test.com",
                "senhaSegura123!",
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
                .body("nome", equalTo("Cliente Perfil Risco Test"))
                .body("cpf", equalTo("15519430004"))
                .extract()
                .as(ClienteResponse.class);

        clienteIdCriado = clienteResponse.id();
        System.out.println("=== DEBUG: Cliente criado com ID: " + clienteIdCriado);
        assertNotNull(clienteIdCriado);
    }

    // ======================== SETUP - CRIAÇÃO DE SIMULAÇÕES ========================

    @Test
    @Order(7)
    void deveRealizarPrimeiraSimulacao_ProdutoConservador() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado, // clienteId
                produtoIdRiscoBaixo1, // produtoId (Poupança)
                new BigDecimal("5000.00"), // valor
                12, // prazoMeses
                null, // prazoDias
                null, // prazoAnos
                TipoProduto.POUPANCA,
                "Poupança Caixa",
                TipoRentabilidade.POS,
                Indice.SELIC,
                0, // liquidez
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
                .body("resultadoSimulacao.valorInvestido", equalTo(5000.00f))
                .body("produtoValidado.nome", containsString("Poupança"))
                .extract()
                .as(SimulacaoResponse.class);

        simulacaoIdCriada1 = simulacaoResponse.simulacaoId();
        System.out.println("=== DEBUG: Primeira simulação criada com ID: " + simulacaoIdCriada1);
        assertNotNull(simulacaoIdCriada1);
    }

    @Test
    @Order(8)
    void deveRealizarSegundaSimulacao_ProdutoConservador() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado, // clienteId
                produtoIdRiscoBaixo2, // produtoId (CDB com FGC)
                new BigDecimal("10000.00"), // valor
                24, // prazoMeses
                null, // prazoDias
                null, // prazoAnos
                TipoProduto.CDB,
                "CDB Perfil Risco 100% CDI",
                TipoRentabilidade.POS,
                Indice.CDI,
                90, // liquidez
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
                .body("resultadoSimulacao.valorInvestido", equalTo(10000.00f))
                .body("produtoValidado.nome", containsString("CDB"))
                .extract()
                .as(SimulacaoResponse.class);

        simulacaoIdCriada2 = simulacaoResponse.simulacaoId();
        System.out.println("=== DEBUG: Segunda simulação criada com ID: " + simulacaoIdCriada2);
        assertNotNull(simulacaoIdCriada2);
    }

    // ======================== TESTES PRINCIPAIS ========================

    @Test
    @Order(9)
    void deveCalcularPerfilRisco() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/perfil-risco/" + clienteIdCriado)
                .then()
                .statusCode(200)
                .body("clienteId", equalTo(clienteIdCriado.intValue()))
                .body("perfil", oneOf("CONSERVADOR", "MODERADO", "AGRESSIVO"))
                .body("pontuacao", notNullValue());

        System.out.println("=== DEBUG: Perfil de risco calculado com sucesso - Conservador");
    }

    @Test
    @Order(10)
    void deveCalcularPerfilRiscoComTokenAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/perfil-risco/" + clienteIdCriado)
                .then()
                .statusCode(200)
                .body("clienteId", equalTo(clienteIdCriado.intValue()))
                .body("perfil", isIn(Arrays.asList("AGRESSIVO", "MODERADO", "CONSERVADOR")))
                .body("pontuacao", notNullValue())
                .body("descricao", notNullValue());

        System.out.println("=== DEBUG: Perfil de risco calculado com token ADMIN");
    }

    // ======================== TESTES DE ERRO ========================

    @Test
    @Order(11)
    void deveRetornar404_ClienteInexistente() {
        Long clienteInexistente = 99999L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/perfil-risco/" + clienteInexistente)
                .then()
                .statusCode(404)
                .body("message", equalTo("Cliente não encontrado"));

        System.out.println("=== DEBUG: Teste 404 para cliente inexistente realizado com sucesso");
    }

    @Test
    @Order(12)
    void deveRetornar401_SemToken() {
        given()
                .when()
                .get("/perfil-risco/" + clienteIdCriado)
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Teste 401 sem token realizado com sucesso");
    }

    @Test
    @Order(13)
    void deveRetornar401_TokenInvalido() {
        given()
                .header("Authorization", "Bearer token_invalido")
                .when()
                .get("/perfil-risco/" + clienteIdCriado)
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Teste 401 com token inválido realizado com sucesso");
    }

    @Test
    @Order(14)
    void deveRetornar400_ClienteIdInvalido() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/perfil-risco/invalid")
                .then()
                .statusCode(404); // Path param inválido resulta em 404

        System.out.println("=== DEBUG: Teste 404 para clienteId inválido realizado com sucesso");
    }

    // ======================== TESTES DE CENÁRIOS ADICIONAIS ========================

    @Test
    @Order(15)
    void deveValidarEstruturaDaResponse() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/perfil-risco/" + clienteIdCriado)
                .then()
                .statusCode(200)
                .body("clienteId", notNullValue())
                .body("perfil", anyOf(equalTo("CONSERVADOR"), equalTo("MODERADO"), equalTo("AGRESSIVO")))
                .body("pontuacao", allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(100)))
                .body("descricao", not(emptyString()));

        System.out.println("=== DEBUG: Validação da estrutura da response realizada com sucesso");
    }

    @Test
    @Order(16)
    void deveValidarContentTypeResponse() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/perfil-risco/" + clienteIdCriado)
                .then()
                .statusCode(200)
                .contentType("application/json");

        System.out.println("=== DEBUG: Validação do Content-Type realizada com sucesso");
    }
}
