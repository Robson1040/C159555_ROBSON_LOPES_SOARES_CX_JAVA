package br.gov.caixa.api.investimentos.resource.fluxo;

import br.gov.caixa.api.investimentos.dto.autenticacao.LoginRequest;
import br.gov.caixa.api.investimentos.dto.autenticacao.LoginResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoResponse;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponse;

import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.service.autenticacao.JwtService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração que demonstra o fluxo completo do sistema:
 * 1. Cadastra um produto
 * 2. Cadastra um cliente com role USER  
 * 3. Obtém token de autenticação do cliente
 * 4. Realiza investimento usando o token do cliente
 * 5. Realiza simulação de investimento usando o token do cliente
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class FluxoCompletoIntegrationTest {

    @Inject
    JwtService jwtService;

    private String adminToken;
    private static Long produtoId;
    private static Long clienteId;
    private static String clienteToken;

    @BeforeEach
    void setUp() {
        // Token de admin para operações administrativas
        adminToken = jwtService.gerarToken("admin@test.com", "ADMIN");
    }

    @Test
    @Order(1)
    void deveCadastrarProduto() {
        System.out.println("=== STEP 1: Cadastrando produto CDB Premium ===");
        
        ProdutoRequest produto = new ProdutoRequest(
                "CDB Premium Fluxo Teste",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("110.5"), // 110.5% do CDI
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                30, // liquidez em dias
                30, // mínimo de dias de investimento
                true // tem FGC
        );

        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produto)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("CDB Premium Fluxo Teste"))
                .body("tipo", equalTo("CDB"))
                .body("tipo_rentabilidade", equalTo("POS"))
                .body("rentabilidade", equalTo(110.5f))
                .body("fgc", equalTo(true))
                .extract()
                .as(ProdutoResponse.class);

        produtoId = produtoResponse.id();
        System.out.println("✅ Produto criado com ID: " + produtoId);
        assertNotNull(produtoId);
    }

    @Test
    @Order(2)
    void deveCadastrarCliente() {
        System.out.println("=== STEP 2: Cadastrando cliente com role USER ===");
        
        ClienteRequest cliente = new ClienteRequest(
                "joao Investidora Santos",
                "05447756006", // CPF válido para teste
                "joao.investidora@test.com",
                "senha@123",
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
                .body("nome", equalTo("joao Investidora Santos"))
                .body("cpf", equalTo("05447756006"))
                .body("username", equalTo("joao.investidora@test.com"))
                .body("role", equalTo("USER"))
                .extract()
                .as(ClienteResponse.class);

        clienteId = clienteResponse.id();
        System.out.println("✅ Cliente criado com ID: " + clienteId);
        assertNotNull(clienteId);
    }

    @Test
    @Order(3)
    void deveObterTokenDoCliente() {
        System.out.println("=== STEP 3: Obtendo token de autenticação do cliente ===");
        
        LoginRequest loginRequest = new LoginRequest(
                "joao.investidora@test.com",
                "senha@123"
        );

        LoginResponse loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/entrar")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("usuario", equalTo("joao.investidora@test.com"))
                .body("role", equalTo("USER"))
                .extract()
                .as(LoginResponse.class);

        clienteToken = loginResponse.token();
        System.out.println("✅ Token obtido: " + clienteToken.substring(0, 50) + "...");
        assertNotNull(clienteToken);
        assertFalse(clienteToken.isEmpty());
        System.out.println(this.clienteToken);
    }

    @Test
    @Order(4)
    void deveRealizarInvestimento() {
        System.out.println("=== STEP 4: Realizando investimento com token do cliente ===");
        System.out.println(this.clienteToken);

        InvestimentoRequest investimento = new InvestimentoRequest(
                clienteId.longValue(),
                produtoId.longValue(),
                new BigDecimal("5000.00"), // valor do investimento
                24, // prazo em meses
                null,  // prazo em dias
                null,  // prazo em anos
                LocalDate.now()
        );

        InvestimentoResponse investimentoResponse = given()
                .header("Authorization", "Bearer " + clienteToken)
                .contentType(ContentType.JSON)
                .body(investimento)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteId.intValue()))
                .body("produtoId", equalTo(produtoId.intValue()))
                .body("valor", equalTo(5000.0f))
                .body("prazoMeses", equalTo(24))
                .body("tipo", equalTo("CDB"))
                .body("fgc", equalTo(true))
                .extract()
                .as(InvestimentoResponse.class);

        System.out.println("✅ Investimento realizado com ID: " + investimentoResponse.id());
        assertNotNull(investimentoResponse.id());
        assertEquals(clienteId, investimentoResponse.clienteId());
        assertEquals(produtoId, investimentoResponse.produtoId());
    }

    @Test
    @Order(5)
    void deveRealizarSimulacaoInvestimento() {
        System.out.println("=== STEP 5: Realizando simulação de investimento com token do cliente ===");
        
        SimulacaoRequest simulacao = new SimulacaoRequest(
                clienteId.longValue(),
                produtoId.longValue(),
                new BigDecimal("10000.00"), // valor da simulação
                18, // prazoMeses
                null,  // prazoDias
                null,  // prazoAnos
                TipoProduto.CDB,
                "CDB Premium Fluxo Teste", // nome do produto
                TipoRentabilidade.POS,
                Indice.CDI,
                30, // liquidez
                true // FGC
        );

        SimulacaoResponse simulacaoResponse = given()
                .header("Authorization", "Bearer " + clienteToken)
                .contentType(ContentType.JSON)
                .body(simulacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(201)
                .body("clienteId", equalTo(clienteId.intValue()))
                .body("produtoValidado.id", equalTo(produtoId.intValue()))
                .body("produtoValidado.nome", equalTo("CDB Premium Fluxo Teste"))
                .body("resultadoSimulacao.rendimento", greaterThan(0.0f))
                .body("resultadoSimulacao.rentabilidadeEfetiva", greaterThan(0.0f))
                .body("produtoValidado.fgc", equalTo(true))
                .extract()
                .as(SimulacaoResponse.class);

        System.out.println("✅ Simulação realizada:");
        System.out.println("   Valor investido: R$ " + simulacaoResponse.resultadoSimulacao().valorInvestido());
        System.out.println("   Valor final: R$ " + simulacaoResponse.resultadoSimulacao().valorFinal());
        System.out.println("   Rendimento: R$ " + simulacaoResponse.resultadoSimulacao().rendimento());
        System.out.println("   Rentabilidade efetiva: " + simulacaoResponse.resultadoSimulacao().rentabilidadeEfetiva() + "%");

        assertNotNull(simulacaoResponse);
        assertEquals(clienteId, simulacaoResponse.clienteId());
        assertTrue(simulacaoResponse.resultadoSimulacao().valorFinal().compareTo(simulacaoResponse.resultadoSimulacao().valorInvestido()) > 0);
        assertTrue(simulacaoResponse.resultadoSimulacao().rendimento().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @Order(6)
    void deveValidarSeguranca_ClienteNaoPodeAcessarOutroCliente() {
        System.out.println("=== STEP 6: Validando segurança - cliente não pode acessar dados de outro cliente ===");
        
        // Tentar criar investimento para outro cliente (deve falhar com 403)
        InvestimentoRequest investimentoInvalido = new InvestimentoRequest(
                999L, // ID de outro cliente fictício
                produtoId,
                new BigDecimal("1000.00"),
                12, null, null,
                LocalDate.now()
        );

        given()
                .header("Authorization", "Bearer " + clienteToken)
                .contentType(ContentType.JSON)
                .body(investimentoInvalido)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(403); // Forbidden - não pode acessar dados de outro cliente

        System.out.println("✅ Segurança validada: cliente USER não pode criar investimento para outro cliente");
    }

    @Test
    @Order(7)
    void deveValidarQueAdminTemAcessoTotal() {
        System.out.println("=== STEP 7: Validando que ADMIN tem acesso total ===");
        
        // Admin pode criar investimento para qualquer cliente
        InvestimentoRequest investimentoAdmin = new InvestimentoRequest(
                clienteId,
                produtoId,
                new BigDecimal("2000.00"),
                12, null, null,
                LocalDate.now()
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(investimentoAdmin)
                .when()
                .post("/investimentos")
                .then()
                .statusCode(201)
                .body("valor", equalTo(2000.0f));

        System.out.println("✅ Validado: ADMIN pode criar investimentos para qualquer cliente");
    }

    @Test
    @Order(8)
    void deveValidarConsultaInvestimentos() {
        System.out.println("=== STEP 8: Validando consulta de investimentos do cliente ===");
        
        // Cliente pode consultar seus próprios investimentos
        given()
                .header("Authorization", "Bearer " + clienteToken)
                .when()
                .get("/investimentos/" + clienteId)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2)) // Pelo menos 2 investimentos criados
                .body("[0].clienteId", equalTo(clienteId.intValue()))
                .body("[0].tipo", equalTo("CDB"));

        System.out.println("✅ Cliente pode consultar seus próprios investimentos");

        // Cliente não pode consultar investimentos de outro cliente
        given()
                .header("Authorization", "Bearer " + clienteToken)
                .when()
                .get("/investimentos/999") // ID fictício de outro cliente
                .then()
                .statusCode(403); // Forbidden

        System.out.println("✅ Cliente não pode consultar investimentos de outro cliente");
    }
}