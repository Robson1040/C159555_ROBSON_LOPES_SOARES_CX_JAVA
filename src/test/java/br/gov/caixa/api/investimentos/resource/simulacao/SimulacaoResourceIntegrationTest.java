package br.gov.caixa.api.investimentos.resource.simulacao;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimulacaoResourceIntegrationTest {

    @Inject
    JwtService jwtService;

    // Tokens para diferentes tipos de usuário
    private String adminToken;
    private String userToken;

    // IDs dos dados criados para reutilização nos testes
    private static Long produtoIdCriado1;
    private static Long produtoIdCriado2; 
    private static Long produtoIdCriado3;
    private static Long clienteIdCriado1;
    private static Long clienteIdCriado2;
    private static Long simulacaoIdCriada1;
    private static Long simulacaoIdCriada2;
    private static Long simulacaoIdCriada3;

    @BeforeEach
    public void setup() {
        RestAssured.port = 8081;
        adminToken = jwtService.gerarToken("admin_sim@test.com", "ADMIN");
        userToken = jwtService.gerarToken("user_sim@test.com", "USER");
    }

    // ==================== SETUP: Criação de Produtos ====================

    @Test
    @Order(1)
    void deveCriarProdutoParaSimulacao1() {
        ProdutoRequest produto = new ProdutoRequest(
                "CDB Simulação Resource 100% CDI",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("100.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                90,
                180,
                true
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("CDB Simulação Resource 100% CDI"))
                .body("tipo", equalTo("CDB"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCriado1 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto SIM 1 criado com ID: " + produtoIdCriado1);
        assertNotNull(produtoIdCriado1);
    }

    @Test
    @Order(2)
    void deveCriarProdutoParaSimulacao2() {
        ProdutoRequest produto = new ProdutoRequest(
                "Tesouro IPCA+ 2030 Resource",
                TipoProduto.TESOURO_DIRETO,
                TipoRentabilidade.POS,
                new BigDecimal("6.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.IPCA,
                1,
                1825, // 5 anos
                true
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Tesouro IPCA+ 2030 Resource"))
                .body("tipo", equalTo("TESOURO_DIRETO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCriado2 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto SIM 2 criado com ID: " + produtoIdCriado2);
        assertNotNull(produtoIdCriado2);
    }

    @Test
    @Order(3)
    void deveCriarProdutoParaSimulacao3() {
        ProdutoRequest produto = new ProdutoRequest(
                "LCI Pré-fixada Resource 8%",
                TipoProduto.LCI,
                TipoRentabilidade.PRE,
                new BigDecimal("8.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.NENHUM,
                180,
                720, // 2 anos
                true
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("LCI Pré-fixada Resource 8%"))
                .body("tipo", equalTo("LCI"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCriado3 = produtoResponse.id();
        System.out.println("=== DEBUG: Produto SIM 3 criado com ID: " + produtoIdCriado3);
        assertNotNull(produtoIdCriado3);
    }

    // ==================== SETUP: Criação de Clientes ====================

    @Test
    @Order(4)
    void deveCriarClienteParaSimulacao1() {
        ClienteRequest cliente = new ClienteRequest(
                "Carlos Simulação Resource",
                "11144477735", // CPF válido diferente
                "carlos_sim@test.com",
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
                .body("nome", equalTo("Carlos Simulação Resource"))
                .body("cpf", equalTo("11144477735"))
                .extract()
                .as(ClienteResponse.class);

        clienteIdCriado1 = clienteResponse.id();
        System.out.println("=== DEBUG: Cliente SIM 1 criado com ID: " + clienteIdCriado1);
        assertNotNull(clienteIdCriado1);
    }

    @Test
    @Order(5)
    void deveCriarClienteParaSimulacao2() {
        ClienteRequest cliente = new ClienteRequest(
                "Ana Simulação Resource",
                "98765432100", // CPF válido diferente
                "ana_sim@test.com",
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
                .body("nome", equalTo("Ana Simulação Resource"))
                .body("cpf", equalTo("98765432100"))
                .extract()
                .as(ClienteResponse.class);

        clienteIdCriado2 = clienteResponse.id();
        System.out.println("=== DEBUG: Cliente SIM 2 criado com ID: " + clienteIdCriado2);
        assertNotNull(clienteIdCriado2);
    }

    // ==================== SETUP: Criação de Simulações ====================

    @Test
    @Order(6)
    void deveRealizarPrimeiraSimulacao() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado1, // clienteId
                produtoIdCriado1, // produtoId
                new BigDecimal("10000.00"), // valor
                null, // prazoMeses
                365, // prazoDias - 1 ano
                null, // prazoAnos
                null, // tipoProduto
                null, // nome
                null, // tipoRentabilidade
                null, // indice
                null, // liquidez
                null  // fgc
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

        simulacaoIdCriada1 = simulacaoResponse.simulacaoId();
        System.out.println("=== DEBUG: Simulação SIM 1 criada com ID: " + simulacaoIdCriada1);
        assertNotNull(simulacaoIdCriada1);
    }

    @Test
    @Order(7)
    void deveRealizarSegundaSimulacao() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado1, // clienteId
                produtoIdCriado2, // produtoId
                new BigDecimal("25000.00"), // valor
                null, // prazoMeses
                730, // prazoDias - 2 anos
                null, // prazoAnos
                null, // tipoProduto
                null, // nome
                null, // tipoRentabilidade
                null, // indice
                null, // liquidez
                null  // fgc
        );

        SimulacaoResponse simulacaoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201)
                .body("resultadoSimulacao.valorInvestido", equalTo(25000.00f))
                .body("produtoValidado.nome", containsString("Tesouro"))
                .extract()
                .as(SimulacaoResponse.class);

        simulacaoIdCriada2 = simulacaoResponse.simulacaoId();
        System.out.println("=== DEBUG: Simulação SIM 2 criada com ID: " + simulacaoIdCriada2);
        assertNotNull(simulacaoIdCriada2);
    }

    @Test
    @Order(8)
    void deveRealizarTerceiraSimulacao() {
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteIdCriado2, // clienteId
                produtoIdCriado3, // produtoId
                new BigDecimal("15000.00"), // valor
                null, // prazoMeses
                540, // prazoDias - 1.5 anos
                null, // prazoAnos
                null, // tipoProduto
                null, // nome
                null, // tipoRentabilidade
                null, // indice
                null, // liquidez
                null  // fgc
        );

        SimulacaoResponse simulacaoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201)
                .body("resultadoSimulacao.valorInvestido", equalTo(15000.00f))
                .body("produtoValidado.nome", containsString("LCI"))
                .extract()
                .as(SimulacaoResponse.class);

        simulacaoIdCriada3 = simulacaoResponse.simulacaoId();
        System.out.println("=== DEBUG: Simulação SIM 3 criada com ID: " + simulacaoIdCriada3);
        assertNotNull(simulacaoIdCriada3);
    }

    // ==================== TESTES PRINCIPAIS DO SIMULACAO RESOURCE ====================

    @Test
    @Order(9)
    void deveListarTodasSimulacoes() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2)) // Pelo menos as 3 que criamos
                .body("id", hasItems(simulacaoIdCriada1.intValue(), simulacaoIdCriada2.intValue(), simulacaoIdCriada3.intValue()));

        System.out.println("=== DEBUG: Listagem de todas as simulações realizada com sucesso");
    }

    @Test
    @Order(10)
    void deveConsultarAgrupamentoPorProdutoDia() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("produto", hasItems(
                    containsString("CDB"),
                    containsString("Tesouro")
                ));

        System.out.println("=== DEBUG: Consulta agrupamento por produto/dia realizada com sucesso");
    }

    @Test
    @Order(11)
    void deveConsultarAgrupamentoPorProdutoMes() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simulacoes/por-produto-mes")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("produto", hasItems(
                    containsString("CDB"),
                    containsString("Tesouro")
                ));

        System.out.println("=== DEBUG: Consulta agrupamento por produto/mês realizada com sucesso");
    }

    @Test
    @Order(12)
    void deveConsultarAgrupamentoPorProdutoAno() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/simulacoes/por-produto-ano")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("produto", hasItems(
                    containsString("CDB"),
                    containsString("Tesouro")
                ));

        System.out.println("=== DEBUG: Consulta agrupamento por produto/ano realizada com sucesso");
    }

    // ==================== TESTES DE AUTORIZAÇÃO ====================

    @Test
    @Order(13)
    void deveNegaAcessoParaUsuarioNaoAdmin_ListarSimulacoes() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(403);

        System.out.println("=== DEBUG: Teste de autorização (403) para listagem realizado com sucesso");
    }

    @Test
    @Order(14)
    void deveNegaAcessoParaUsuarioNaoAdmin_AgrupamentoPorDia() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(403);

        System.out.println("=== DEBUG: Teste de autorização (403) para agrupamento/dia realizado com sucesso");
    }

    @Test
    @Order(15)
    void deveNegaAcessoParaUsuarioNaoAdmin_AgrupamentoPorMes() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/simulacoes/por-produto-mes")
                .then()
                .statusCode(403);

        System.out.println("=== DEBUG: Teste de autorização (403) para agrupamento/mês realizado com sucesso");
    }

    @Test
    @Order(16)
    void deveNegaAcessoParaUsuarioNaoAdmin_AgrupamentoPorAno() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/simulacoes/por-produto-ano")
                .then()
                .statusCode(403);

        System.out.println("=== DEBUG: Teste de autorização (403) para agrupamento/ano realizado com sucesso");
    }

    // ==================== TESTES DE CASOS SEM TOKEN ====================

    @Test
    @Order(17)
    void deveNegaAcessoSemToken_ListarSimulacoes() {
        given()
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Teste sem token (401) para listagem realizado com sucesso");
    }

    @Test
    @Order(18)
    void deveNegaAcessoSemToken_AgrupamentoPorDia() {
        given()
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Teste sem token (401) para agrupamento/dia realizado com sucesso");
    }

    @Test
    @Order(19)
    void deveNegaAcessoSemToken_AgrupamentoPorMes() {
        given()
                .when()
                .get("/simulacoes/por-produto-mes")
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Teste sem token (401) para agrupamento/mês realizado com sucesso");
    }

    @Test
    @Order(20)
    void deveNegaAcessoSemToken_AgrupamentoPorAno() {
        given()
                .when()
                .get("/simulacoes/por-produto-ano")
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Teste sem token (401) para agrupamento/ano realizado com sucesso");
    }
}
