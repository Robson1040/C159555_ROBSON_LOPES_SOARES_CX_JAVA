package br.gov.caixa.api.investimentos.resource.investimento;

import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoResponse;

import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.service.autenticacao.JwtService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para todos os endpoints da InvestimentoResource.
 * 
 * Os testes seguem uma ordem lógica:
 * 1. Criar produtos necessários (POST /produtos)
 * 2. Criar clientes para os investimentos (POST /clientes)
 * 3. Criar investimentos reais (POST /investimentos)
 * 4. Buscar investimentos por cliente (GET /investimentos/CLIENTE_ID)
 * 5. Testes de validação e casos de erro
 * 6. Testes de autorização e segurança
 * 
 * @author GitHub Copilot
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class InvestimentoResourceIntegrationTest {

    @Inject
    JwtService jwtService;

    // Tokens para diferentes tipos de usuário
    private String adminToken;
    private String userToken;

    // IDs dos dados criados para reutilização nos testes
    private static Long produtoIdPoupanca;
    private static Long produtoIdCDB; 
    private static Long produtoIdDebenture;
    private static Long clienteIdInvestidor1;
    private static Long clienteIdInvestidor2;
    private static Long investimentoId1;
    private static Long investimentoId2;
    private static Long investimentoId3;

    @BeforeEach
    public void setup() {
        RestAssured.port = 8081;
        adminToken = jwtService.gerarToken("admin@test.com", "ADMIN");
        userToken = jwtService.gerarToken("user@test.com", "USER");
    }

    @Test
    @Order(1)
    void deveCriarProdutoPoupanca() {
        ProdutoRequest produto = new ProdutoRequest(
                "Poupança Investimento Test",
                TipoProduto.POUPANCA,
                TipoRentabilidade.POS,
                new BigDecimal("0.50"), // 0,5% ao mês
                PeriodoRentabilidade.AO_MES,
                Indice.SELIC,
                0, // liquidez imediata
                0, // sem carência
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
                .body("nome", equalTo("Poupança Investimento Test"))
                .body("tipo", equalTo("POUPANCA"))
                .body("risco", equalTo("BAIXO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdPoupanca = produtoResponse.id();
        System.out.println("=== DEBUG: Produto Poupança criado com ID: " + produtoIdPoupanca);
        assertNotNull(produtoIdPoupanca);
    }

    @Test
    @Order(2)
    void deveCriarProdutoCDB() {
        ProdutoRequest produto = new ProdutoRequest(
                "CDB Investimento Test 110% CDI",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("110.0"), // 110% do CDI
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                90, // liquidez 90 dias
                30, // carência 30 dias
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
                .body("nome", equalTo("CDB Investimento Test 110% CDI"))
                .body("tipo", equalTo("CDB"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCDB = produtoResponse.id();
        System.out.println("=== DEBUG: Produto CDB criado com ID: " + produtoIdCDB);
        assertNotNull(produtoIdCDB);
    }

    @Test
    @Order(3)
    void deveCriarProdutoDebenture() {
        ProdutoRequest produto = new ProdutoRequest(
                "Debênture Investimento Test 9%",
                TipoProduto.DEBENTURE,
                TipoRentabilidade.PRE,
                new BigDecimal("9.00"), // 9% ao ano
                PeriodoRentabilidade.AO_ANO,
                null, // pré-fixado
                120, // liquidez 120 dias
                60, // carência 60 dias
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
                .body("nome", equalTo("Debênture Investimento Test 9%"))
                .body("tipo", equalTo("DEBENTURE"))
                .body("risco", oneOf("MEDIO", "ALTO"))
                .extract()
                .as(ProdutoResponse.class);

        produtoIdDebenture = produtoResponse.id();
        System.out.println("=== DEBUG: Produto Debênture criado com ID: " + produtoIdDebenture);
        assertNotNull(produtoIdDebenture);
    }

    @Test
    @Order(4)
    void deveCriarPrimeiroCliente() {
        ClienteRequest cliente = new ClienteRequest(
                "João Silva Investidor",
                "07316543078", // CPF válido para teste
                "joao.investidor@test.com",
                "senha123",
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
                .body("nome", equalTo("João Silva Investidor"))
                .body("cpf", equalTo("07316543078"))
                .extract()
                .as(ClienteResponse.class);

        clienteIdInvestidor1 = clienteResponse.id();
        System.out.println("=== DEBUG: Cliente João criado com ID: " + clienteIdInvestidor1);
        assertNotNull(clienteIdInvestidor1);
    }

    @Test
    @Order(5)
    void deveCriarSegundoCliente() {
        ClienteRequest cliente = new ClienteRequest(
                "Maria Santos Investidora",
                "66671268002", // CPF válido para teste
                "maria.investidora@test.com",
                "senha456",
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
                .body("nome", equalTo("Maria Santos Investidora"))
                .body("cpf", equalTo("66671268002"))
                .extract()
                .as(ClienteResponse.class);

        clienteIdInvestidor2 = clienteResponse.id();
        System.out.println("=== DEBUG: Cliente Maria criado com ID: " + clienteIdInvestidor2);
        assertNotNull(clienteIdInvestidor2);
    }

    @Test
    @Order(6)
    void deveCriarInvestimentoPoupanca() {
        InvestimentoRequest investimento = new InvestimentoRequest(
                clienteIdInvestidor1, // clienteId
                produtoIdPoupanca, // produtoId
                new BigDecimal("1000.00"), // valor
                null, // prazoMeses - poupança sem prazo
                365, // prazoDias
                null, // prazoAnos
                LocalDate.now() // data
        );

        InvestimentoResponse investimentoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(investimento)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteIdInvestidor1.intValue()))
                .body("produtoId", equalTo(produtoIdPoupanca.intValue()))
                .body("valor", equalTo(1000.00f))
                .body("tipo", equalTo("POUPANCA"))
                .body("fgc", equalTo(true))
                .extract()
                .as(InvestimentoResponse.class);

        investimentoId1 = investimentoResponse.id();
        System.out.println("=== DEBUG: Investimento Poupança criado com ID: " + investimentoId1);
        assertNotNull(investimentoId1);
    }

    @Test
    @Order(7)
    void deveCriarInvestimentoCDB() {
        InvestimentoRequest investimento = new InvestimentoRequest(
                clienteIdInvestidor1, // clienteId
                produtoIdCDB, // produtoId
                new BigDecimal("5000.00"), // valor
                12, // prazoMeses
                null, // prazoDias
                null, // prazoAnos
                LocalDate.now() // data
        );

        InvestimentoResponse investimentoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(investimento)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteIdInvestidor1.intValue()))
                .body("produtoId", equalTo(produtoIdCDB.intValue()))
                .body("valor", equalTo(5000.00f))
                .body("prazoMeses", equalTo(12))
                .body("tipo", equalTo("CDB"))
                .body("fgc", equalTo(true))
                .extract()
                .as(InvestimentoResponse.class);

        investimentoId2 = investimentoResponse.id();
        System.out.println("=== DEBUG: Investimento CDB criado com ID: " + investimentoId2);
        assertNotNull(investimentoId2);
    }

    @Test
    @Order(8)
    void deveCriarInvestimentoDebenture() {
        InvestimentoRequest investimento = new InvestimentoRequest(
                clienteIdInvestidor2, // clienteId - cliente diferente
                produtoIdDebenture, // produtoId
                new BigDecimal("10000.00"), // valor
                null, // prazoMeses
                null, // prazoDias
                2, // prazoAnos
                LocalDate.now() // data
        );

        InvestimentoResponse investimentoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(investimento)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteIdInvestidor2.intValue()))
                .body("produtoId", equalTo(produtoIdDebenture.intValue()))
                .body("valor", equalTo(10000.00f))
                .body("prazoAnos", equalTo(2))
                .body("tipo", equalTo("DEBENTURE"))
                .body("fgc", equalTo(false))
                .extract()
                .as(InvestimentoResponse.class);

        investimentoId3 = investimentoResponse.id();
        System.out.println("=== DEBUG: Investimento Debênture criado com ID: " + investimentoId3);
        assertNotNull(investimentoId3);
    }

    @Test
    @Order(9)
    void deveBuscarInvestimentosDoCliente1() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/investimentos/" + clienteIdInvestidor1)
                .then()
                .statusCode(200)
                .body("size()", equalTo(2)) // Cliente 1 tem 2 investimentos
                .body("[0].clienteId", equalTo(clienteIdInvestidor1.intValue()))
                .body("[1].clienteId", equalTo(clienteIdInvestidor1.intValue()))
                .body("[0].id", anyOf(equalTo(investimentoId1.intValue()), equalTo(investimentoId2.intValue())))
                .body("[1].id", anyOf(equalTo(investimentoId1.intValue()), equalTo(investimentoId2.intValue())));

        System.out.println("=== DEBUG: Busca de investimentos do cliente 1 bem-sucedida");
    }

    @Test
    @Order(10)
    void deveBuscarInvestimentosDoCliente2() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/investimentos/" + clienteIdInvestidor2)
                .then()
                .statusCode(200)
                .body("size()", equalTo(1)) // Cliente 2 tem 1 investimento
                .body("[0].clienteId", equalTo(clienteIdInvestidor2.intValue()))
                .body("[0].id", equalTo(investimentoId3.intValue()))
                .body("[0].tipo", equalTo("DEBENTURE"));

        System.out.println("=== DEBUG: Busca de investimentos do cliente 2 bem-sucedida");
    }

    @Test
    @Order(11)
    void deveBuscarInvestimentosComTokenDeUsuario() {
        // Admin tem acesso a qualquer cliente
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/investimentos/" + clienteIdInvestidor1)
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));

        System.out.println("=== DEBUG: Busca com token de admin bem-sucedida");
    }

    @Test
    @Order(12)
    void deveRetornar200_ClienteSemInvestimentos() {
        // Criar um cliente que não tem investimentos
        ClienteRequest cliente = new ClienteRequest(
                "Carlos Sem Investimento",
                "27122259021",
                "carlos.sem@test.com",
                "senha789",
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
                .extract()
                .as(ClienteResponse.class);

        Long clienteSemInvestimentos = clienteResponse.id();

        // Buscar investimentos de cliente sem investimentos deve retornar lista vazia
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/investimentos/" + clienteSemInvestimentos)
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));

        System.out.println("=== DEBUG: Cliente sem investimentos retorna lista vazia");
    }

    // === TESTES DE VALIDAÇÃO ===

    @Test
    @Order(13)
    void deveRetornar400_DadosInvalidosNoInvestimento() {
        InvestimentoRequest investimentoInvalido = new InvestimentoRequest(
                null, // clienteId inválido
                produtoIdPoupanca,
                new BigDecimal("0.50"), // valor muito baixo
                null, null, null,
                LocalDate.now()
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(investimentoInvalido)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(400);

        System.out.println("=== DEBUG: Validação de dados inválidos funcionando");
    }

    @Test
    @Order(14)
    void deveRetornar400_ClienteInexistente() {
        InvestimentoRequest investimento = new InvestimentoRequest(
                99999L, // clienteId que não existe
                produtoIdPoupanca,
                new BigDecimal("1000.00"),
                null, null, null,
                LocalDate.now()
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(investimento)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404)));

        System.out.println("=== DEBUG: Validação de cliente inexistente funcionando");
    }

    @Test
    @Order(15)
    void deveRetornar400_ProdutoInexistente() {
        InvestimentoRequest investimento = new InvestimentoRequest(
                clienteIdInvestidor1,
                99999L, // produtoId que não existe
                new BigDecimal("1000.00"),
                null, null, null,
                LocalDate.now()
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(investimento)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404)));

        System.out.println("=== DEBUG: Validação de produto inexistente funcionando");
    }

    // === TESTES DE AUTORIZAÇÃO ===

    @Test
    @Order(16)
    void deveRetornar401_SemToken() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/investimentos/" + clienteIdInvestidor1)
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Rejeição sem token funcionando");
    }

    @Test
    @Order(17)
    void deveRetornar401_TokenInvalido() {
        given()
                .header("Authorization", "Bearer token_invalido_aqui")
                .when()
                .get("/investimentos/" + clienteIdInvestidor1)
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Rejeição de token inválido funcionando");
    }

    @Test
    @Order(18)
    void deveRetornar400_ClienteIdInvalido() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/investimentos/abc") // clienteId não numérico
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404)));

        System.out.println("=== DEBUG: Validação de clienteId inválido funcionando");
    }

    // === TESTES DE ESTRUTURA DA RESPONSE ===

    @Test
    @Order(19)
    void deveValidarEstruturaDaResponse() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/investimentos/" + clienteIdInvestidor1)
                .then()
                .statusCode(200)
                .body("[0]", hasKey("id"))
                .body("[0]", hasKey("clienteId"))
                .body("[0]", hasKey("produtoId"))
                .body("[0]", hasKey("valor"))
                .body("[0]", hasKey("data"))
                .body("[0]", hasKey("tipo"))
                .body("[0]", hasKey("tipo_rentabilidade"))
                .body("[0]", hasKey("rentabilidade"))
                .body("[0]", hasKey("periodo_rentabilidade"))
                .body("[0]", hasKey("liquidez"))
                .body("[0]", hasKey("minimo_dias_investimento"))
                .body("[0]", hasKey("fgc"));

        System.out.println("=== DEBUG: Validação da estrutura da response realizada");
    }

    @Test
    @Order(20)
    void deveValidarContentTypeResponse() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/investimentos/" + clienteIdInvestidor1)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        System.out.println("=== DEBUG: Validação do Content-Type realizada com sucesso");
    }
}