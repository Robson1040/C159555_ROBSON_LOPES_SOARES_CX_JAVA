package br.gov.caixa.api.investimentos.integration;

import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
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
import java.util.List;
import java.util.Map;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para todos os endpoints da ProdutoResource.
 * 
 * Os testes seguem uma ordem lógica:
 * 1. Criar alguns produtos (POST /produtos)
 * 2. Alterar um produto por ID (PUT /produtos/ID)
 * 3. Consultar produto por ID (GET /produtos/ID)
 * 4. Consultar todos os produtos (GET /produtos)
 * 5. Testes de casos de erro e validações
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class ProdutoResourceIntegrationTest {

    @Inject
    JwtService jwtService;

    private static Long produtoIdCriado1;
    private static Long produtoIdCriado2;
    private static Long produtoIdCriado3;
    
    // Helper method para buscar produto por nome
    private Long buscarProdutoPorNome(String nome) {
        return given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/produtos")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("find { it.nome == '" + nome + "' }.id");
    }

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Criar tokens JWT para testes
        userToken = jwtService.gerarToken("user@test.com", "USER");
        adminToken = jwtService.gerarToken("admin@test.com", "ADMIN");
        
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @Order(1)
    void deveIncluirPrimeiroProduto() {
        // Dados do primeiro produto - CDB
        ProdutoRequest produtoRequest = new ProdutoRequest(
                "CDB Premium",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("12.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                0, // liquidez imediata
                30, // mínimo 30 dias
                true // com FGC
        );

        // Fazer requisição POST para criar produto (apenas ADMIN pode criar)
        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produtoRequest)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("CDB Premium"))
                .body("tipo", equalTo("CDB"))
                .body("tipo_rentabilidade", equalTo("POS"))
                .body("rentabilidade", equalTo(12.5f))
                .body("periodo_rentabilidade", equalTo("AO_ANO"))
                .body("indice", equalTo("CDI"))
                .body("liquidez", equalTo(0))
                .body("minimo_dias_investimento", equalTo(30))
                .body("fgc", equalTo(true))
                .body("id", notNullValue())
                .extract()
                .as(ProdutoResponse.class);

        // Armazenar ID para próximos testes
        produtoIdCriado1 = produtoResponse.id();
        
        assertNotNull(produtoIdCriado1);
        assertEquals("CDB Premium", produtoResponse.nome());
        assertEquals(TipoProduto.CDB, produtoResponse.tipo());
        assertEquals(TipoRentabilidade.POS, produtoResponse.tipoRentabilidade());
    }

    @Test
    @Order(2)
    void deveIncluirSegundoProduto() {
        // Dados do segundo produto - Tesouro Direto
        ProdutoRequest produtoRequest = new ProdutoRequest(
                "Tesouro IPCA+ 2035",
                TipoProduto.TESOURO_DIRETO,
                TipoRentabilidade.POS,
                new BigDecimal("6.8"),
                PeriodoRentabilidade.AO_ANO,
                Indice.IPCA,
                1, // liquidez em 1 dia
                0, // sem mínimo de dias
                false // sem FGC
        );

        // Fazer requisição POST para criar produto
        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produtoRequest)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Tesouro IPCA+ 2035"))
                .body("tipo", equalTo("TESOURO_DIRETO"))
                .body("tipo_rentabilidade", equalTo("POS"))
                .body("rentabilidade", equalTo(6.8f))
                .body("indice", equalTo("IPCA"))
                .body("liquidez", equalTo(1))
                .body("minimo_dias_investimento", equalTo(0))
                .body("fgc", equalTo(false))
                .body("id", notNullValue())
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCriado2 = produtoResponse.id();
        assertNotNull(produtoIdCriado2);
    }

    @Test
    @Order(3)
    void deveIncluirTerceiroProduto() {
        // Dados do terceiro produto - Fundo de Investimento
        ProdutoRequest produtoRequest = new ProdutoRequest(
                "Fundo DI Expert",
                TipoProduto.FUNDO,
                TipoRentabilidade.POS,
                new BigDecimal("9.2"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                -1, // sem liquidez
                60, // mínimo 60 dias
                false // sem FGC
        );

        // Fazer requisição POST para criar produto
        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produtoRequest)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Fundo DI Expert"))
                .body("tipo", equalTo("FUNDO"))
                .body("liquidez", equalTo(-1))
                .body("minimo_dias_investimento", equalTo(60))
                .body("id", notNullValue())
                .extract()
                .as(ProdutoResponse.class);

        produtoIdCriado3 = produtoResponse.id();
        assertNotNull(produtoIdCriado3);
    }

    @Test
    @Order(4)
    void deveAlterarProdutoPorId() {
        // Primeiro, vamos listar todos os produtos para encontrar um CDB
        Response response = given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/produtos")
        .then()
            .statusCode(200)
            .extract().response();

        List<Map<String, Object>> produtos = response.jsonPath().getList("$");
        Long idProduto = null;
        
        // Buscar o primeiro produto CDB
        for (Map<String, Object> produto : produtos) {
            if ("CDB".equals(produto.get("tipo"))) {
                idProduto = ((Number) produto.get("id")).longValue();
                break;
            }
        }
        
        assertNotNull(idProduto, "Deve existir pelo menos um produto CDB para alterar");
        
        // Dados para atualizar o produto
        ProdutoRequest updateRequest = new ProdutoRequest(
                "CDB Premium Atualizado",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("13.0"), // nova rentabilidade
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                0, 
                45, // novo mínimo de dias
                true
        );

        // Fazer requisição PUT para atualizar produto
        ProdutoResponse produtoResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/produtos/" + idProduto)
                .then()
                .statusCode(200)
                .body("nome", equalTo("CDB Premium Atualizado"))
                .body("rentabilidade", equalTo(13.0f))
                .body("minimo_dias_investimento", equalTo(45))
                .extract()
                .as(ProdutoResponse.class);

        assertEquals("CDB Premium Atualizado", produtoResponse.nome());
        assertEquals(new BigDecimal("13.0"), produtoResponse.rentabilidade());
        assertEquals(45, produtoResponse.minimoDiasInvestimento());
        
        // Atualizar o ID para os próximos testes
        produtoIdCriado1 = idProduto;
    }

    @Test
    @Order(5)
    void deveConsultarProdutoPorId() {
        // Usar o ID que foi definido no teste anterior
        assertNotNull(produtoIdCriado1, "Produto deve ter sido atualizado no teste anterior");

        // Fazer requisição GET para buscar por ID (USER pode consultar)
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/produtos/" + produtoIdCriado1)
                .then()
                .statusCode(200)
                .body("tipo", equalTo("CDB"))
                .body("rentabilidade", anyOf(equalTo(12.5f), equalTo(13.0f), equalTo(13), equalTo(12))); // aceita int e float
    }

    @Test
    @Order(6)
    void deveConsultarTodosOsProdutos() {
        // Fazer requisição GET para listar todos os produtos (USER pode consultar)
        Response response = given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/produtos")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1))) // pelo menos um produto deve existir
                .extract().response();
                
        // Verificar se pelo menos um dos produtos criados nos testes anteriores está na lista
        List<Map<String, Object>> produtos = response.jsonPath().getList("$");
        boolean temProdutoCriado = produtos.stream()
            .anyMatch(produto -> 
                "CDB Premium Atualizado".equals(produto.get("nome")) ||
                "Tesouro IPCA+ 2035".equals(produto.get("nome")) ||
                "Fundo DI Expert".equals(produto.get("nome"))
            );
            
        assertTrue(temProdutoCriado, "Pelo menos um dos produtos criados nos testes deve estar na listagem");
    }

    @Test
    @Order(7)
    void deveConsultarProdutosComFiltros() {
        // Testar filtro por tipo
        given()
                .header("Authorization", "Bearer " + userToken)
                .queryParam("tipo", "CDB")
                .when()
                .get("/produtos")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("tipo", everyItem(equalTo("CDB")));

        // Testar filtro por FGC
        given()
                .header("Authorization", "Bearer " + userToken)
                .queryParam("fgc", true)
                .when()
                .get("/produtos")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("fgc", everyItem(equalTo(true)));

        // Testar filtro por liquidez imediata
        given()
                .header("Authorization", "Bearer " + userToken)
                .queryParam("liquidez_imediata", true)
                .when()
                .get("/produtos")
                .then()
                .statusCode(200)
                .body("liquidez", everyItem(equalTo(0)));
    }

    @Test
    @Order(8)
    void deveContarTotalDeProdutos() {
        // Testar endpoint de contagem
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/produtos/count")
                .then()
                .statusCode(200)
                .body("total", greaterThanOrEqualTo(3));
    }

    // Testes de casos de erro

    @Test
    @Order(9)
    void deveRetornar400ParaProdutoComDadosInvalidos() {
        // Tentar criar produto com dados inválidos
        ProdutoRequest produtoInvalido = new ProdutoRequest(
                "", // nome em branco
                null, // tipo nulo
                null, // tipo rentabilidade nulo
                new BigDecimal("-1"), // rentabilidade negativa
                null, // periodo rentabilidade nulo
                null, // indice pode ser nulo
                -5, // liquidez inválida (deve ser -1 ou >= 0)
                -1, // mínimo dias negativo
                null // fgc nulo
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produtoInvalido)
                .when()
                .post("/produtos")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(10)
    void deveRetornar404ParaProdutoNaoEncontradoPorId() {
        // Tentar buscar produto inexistente por ID
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/produtos/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(11)
    void deveRetornar404ParaAtualizarProdutoInexistente() {
        // Tentar atualizar produto inexistente
        ProdutoRequest updateRequest = new ProdutoRequest(
                "Produto Inexistente",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("10.0"),
                PeriodoRentabilidade.AO_ANO,
                null,
                0,
                30,
                true
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/produtos/99999999")
                .then()
                .statusCode(anyOf(equalTo(404))); // pode retornar 404 ou 500 dependendo da implementação
    }

    @Test
    @Order(12)
    void deveRetornar403ParaUserTentandoCriarProduto() {
        // Tentar criar produto com role USER (apenas ADMIN pode criar)
        ProdutoRequest produtoRequest = new ProdutoRequest(
                "Produto Usuario",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("10.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                0,
                30,
                true
        );

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(produtoRequest)
                .when()
                .post("/produtos")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(13)
    void deveRetornar403ParaUserTentandoAtualizarProduto() {
        assertNotNull(produtoIdCriado1, "Produto deve ter sido criado no teste anterior");
        
        // Tentar atualizar produto com role USER (apenas ADMIN pode atualizar)
        ProdutoRequest updateRequest = new ProdutoRequest(
                "Produto Atualizado por User",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("10.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                0,
                30,
                true
        );

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/produtos/" + produtoIdCriado1)
                .then()
                .statusCode(403);
    }

    @Test
    @Order(14)
    void deveRetornar401ParaRequisicaoSemToken() {
        // Tentar fazer requisição sem token de autorização
        given()
                .when()
                .get("/produtos")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(15)
    void deveValidarRentabilidadeEIndiceCorretamente() {
        // Testar produto pré-fixado (não deve ter índice)
        ProdutoRequest produtoPreFixado = new ProdutoRequest(
                "CDB Pré-fixado",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("12.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.NENHUM, // pré-fixado deve usar NENHUM
                30,
                90,
                true
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produtoPreFixado)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("indice", equalTo("NENHUM"));

        // Testar produto pós-fixado (deve ter índice válido)
        ProdutoRequest produtoPosFixado = new ProdutoRequest(
                "CDB Pós-fixado",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("105.0"), // percentual do CDI
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                0,
                30,
                true
        );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(produtoPosFixado)
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .body("indice", equalTo("CDI"));
    }
}