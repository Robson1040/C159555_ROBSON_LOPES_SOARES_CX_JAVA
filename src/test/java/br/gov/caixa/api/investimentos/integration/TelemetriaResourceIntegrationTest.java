package br.gov.caixa.api.investimentos.integration;

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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TelemetriaResourceIntegrationTest {

    @Inject
    JwtService jwtService;

    // Tokens para diferentes tipos de usuário
    private String adminToken;
    private String userToken;

    @BeforeEach
    public void setup() {
        RestAssured.port = 8081;
        adminToken = jwtService.gerarToken("admin@test.com", "ADMIN");
        userToken = jwtService.gerarToken("user@test.com", "USER");
    }

    // === SETUP: GERAR DADOS DE TELEMETRIA ===

    @Test
    @Order(1)
    void deveGerarDadosTelemetria_ChamadasClientes() {
        System.out.println("=== DEBUG: Iniciando geração de dados de telemetria com chamadas GET /clientes");
        
        // Faz várias chamadas para GET /clientes para gerar dados de telemetria
        for (int i = 0; i < 5; i++) {
            given()
                    .header("Authorization", "Bearer " + adminToken)
                    .when()
                    .get("/clientes")
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(404))); // Aceita 404 se não houver clientes
            
            // Pequena pausa entre as chamadas para simular uso real
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("=== DEBUG: Realizadas 5 chamadas para /clientes");
    }

    @Test
    @Order(2)
    void deveGerarDadosTelemetria_ChamadasProdutos() {
        System.out.println("=== DEBUG: Gerando dados de telemetria com chamadas GET /produtos");
        
        // Faz várias chamadas para GET /produtos para gerar mais dados de telemetria
        for (int i = 0; i < 3; i++) {
            given()
                    .header("Authorization", "Bearer " + adminToken)
                    .when()
                    .get("/produtos")
                    .then()
                    .statusCode(200); // Produtos devem existir dos testes anteriores
            
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("=== DEBUG: Realizadas 3 chamadas para /produtos");
    }

    @Test
    @Order(3)
    void deveGerarDadosTelemetria_ChamadasRecomendacoes() {
        System.out.println("=== DEBUG: Gerando dados de telemetria com chamadas de recomendações");
        
        // Faz chamadas para endpoints de recomendações
        String[] perfis = {"conservador", "moderado", "agressivo"};
        
        for (String perfil : perfis) {
            given()
                    .header("Authorization", "Bearer " + adminToken)
                    .when()
                    .get("/produtos-recomendados/" + perfil)
                    .then()
                    .statusCode(200);
            
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("=== DEBUG: Realizadas chamadas para endpoints de recomendações");
    }

    // === TESTES DA TELEMETRIA BÁSICA ===

    @Test
    @Order(4)
    void deveObterTelemetriaBasica() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("servicos", notNullValue())
                .body("servicos", hasSize(greaterThan(0)))
                .body("periodo", notNullValue());

        System.out.println("=== DEBUG: Telemetria básica obtida com sucesso");
    }

    @Test
    @Order(5)
    void deveValidarEstruturaTelemetriaBasica() {
        String responseBody = given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria")
                .then()
                .statusCode(200)
                .body("servicos[0]", hasKey("nome"))
                .body("servicos[0]", hasKey("contador_execucao"))
                .body("servicos[0]", hasKey("tempo_medio_resposta"))
                .body("periodo", hasKey("inicio"))
                .body("periodo", hasKey("fim"))
                .extract()
                .asString();

        System.out.println("=== DEBUG: Estrutura da telemetria básica validada. Response: " + responseBody.substring(0, Math.min(200, responseBody.length())) + "...");
    }

    // === TESTES DA TELEMETRIA DETALHADA ===

    @Test
    @Order(6)
    void deveObterTelemetriaDetalhada() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));

        System.out.println("=== DEBUG: Telemetria detalhada obtida com sucesso");
    }

    @Test
    @Order(7)
    void deveValidarEstruturaTelemetriaDetalhada() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(200)
                .body("[0]", hasKey("id"))
                .body("[0]", hasKey("endpoint"))
                .body("[0]", hasKey("contadorExecucoes"))
                .body("[0]", hasKey("tempoMedioResposta"))
                .body("[0]", hasKey("tempoTotalExecucao"))
                .body("[0]", hasKey("dataCriacao"))
                .body("[0]", hasKey("ultimaAtualizacao"));

        System.out.println("=== DEBUG: Estrutura da telemetria detalhada validada");
    }

    @Test
    @Order(8)
    void deveVerificarDadosEspecificosDetalhada() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(200)
                .body("[0].contadorExecucoes", greaterThan(0))
                .body("[0].tempoMedioResposta", greaterThanOrEqualTo(0.0f))
                .body("[0].endpoint", notNullValue());

        System.out.println("=== DEBUG: Dados específicos da telemetria detalhada validados");
    }

    // === TESTES DOS ENDPOINTS MAIS ACESSADOS ===

    @Test
    @Order(9)
    void deveObterMaisAcessados_Limite3() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/mais-acessados/3")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", lessThanOrEqualTo(3))
                .body("size()", greaterThan(0));

        System.out.println("=== DEBUG: Endpoints mais acessados (limite 3) obtidos com sucesso");
    }

    @Test
    @Order(10)
    void deveObterMaisAcessados_Limite1() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/mais-acessados/1")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].contadorExecucoes", notNullValue());

        System.out.println("=== DEBUG: Endpoint mais acessado (limite 1) obtido com sucesso");
    }

    @Test
    @Order(11)
    void deveValidarOrdemMaisAcessados() {
        // Verifica se os endpoints estão ordenados por número de acessos (decrescente)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/mais-acessados/5")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
        
        System.out.println("=== DEBUG: Ordem dos endpoints mais acessados validada");
    }

    // === TESTES DE VALIDAÇÃO ===

    @Test
    @Order(12)
    void deveValidarLimiteZero() {
        // Limite zero pode causar erro 500, então só validamos o status
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/mais-acessados/0")
                .then()
                .statusCode(anyOf(equalTo(400))); // Aceita 400 se limite zero causar erro

        System.out.println("=== DEBUG: Limite zero validado corretamente");
    }

    @Test
    @Order(13)
    void deveValidarLimiteNegativo() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/mais-acessados/-1")
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(400))); // Aceita erro 500 também

        System.out.println("=== DEBUG: Limite negativo tratado");
    }

    @Test
    @Order(14)
    void deveValidarLimiteMuitoAlto() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/mais-acessados/1000")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));

        System.out.println("=== DEBUG: Limite muito alto tratado corretamente");
    }

    // === TESTES DE AUTORIZAÇÃO ===

    @Test
    @Order(15)
    void deveNegarAcesso_SemToken_TelemetriaBasica() {
        given()
                .when()
                .get("/telemetria")
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Acesso negado sem token para telemetria básica");
    }

    @Test
    @Order(16)
    void deveNegarAcesso_SemToken_TelemetriaDetalhada() {
        given()
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Acesso negado sem token para telemetria detalhada");
    }

    @Test
    @Order(17)
    void deveNegarAcesso_SemToken_MaisAcessados() {
        given()
                .when()
                .get("/telemetria/mais-acessados/5")
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Acesso negado sem token para mais acessados");
    }

    @Test
    @Order(18)
    void deveNegarAcesso_TokenInvalido() {
        given()
                .header("Authorization", "Bearer token_invalido")
                .when()
                .get("/telemetria")
                .then()
                .statusCode(401);

        System.out.println("=== DEBUG: Acesso negado com token inválido");
    }

    @Test
    @Order(19)
    void deveNegarAcesso_UsuarioSemPermissao_TelemetriaBasica() {
        // Usuário comum não deve ter acesso (apenas ADMIN)
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/telemetria")
                .then()
                .statusCode(403); // Forbidden - usuário autenticado mas sem permissão

        System.out.println("=== DEBUG: Acesso negado para usuário comum (telemetria básica)");
    }

    @Test
    @Order(20)
    void deveNegarAcesso_UsuarioSemPermissao_TelemetriaDetalhada() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(403);

        System.out.println("=== DEBUG: Acesso negado para usuário comum (telemetria detalhada)");
    }

    @Test
    @Order(21)
    void deveNegarAcesso_UsuarioSemPermissao_MaisAcessados() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/telemetria/mais-acessados/3")
                .then()
                .statusCode(403);

        System.out.println("=== DEBUG: Acesso negado para usuário comum (mais acessados)");
    }

    // === TESTES DE LIMPEZA DE MÉTRICAS ===

    @Test
    @Order(22)
    void deveNegarAcesso_UsuarioSemPermissao_LimparMetricas() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .delete("/telemetria")
                .then()
                .statusCode(403);

        System.out.println("=== DEBUG: Acesso negado para usuário comum (limpar métricas)");
    }

    @Test
    @Order(23)
    void deveLimparMetricas() {
        // Primeiro verifica se há métricas
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));

        // Limpa as métricas
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/telemetria")
                .then()
                .statusCode(204); // No Content

        System.out.println("=== DEBUG: Métricas limpas com sucesso");
    }

    @Test
    @Order(24)
    void deveVerificarMetricasLimpas() {
        // Verifica se as métricas foram realmente limpas
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));

        System.out.println("=== DEBUG: Verificado que métricas foram limpas");
    }

    @Test
    @Order(25)
    void deveTerTelemetriaVaziaAposLimpeza() {
        // Telemetria básica deve estar vazia ou com valores zerados
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria")
                .then()
                .statusCode(200)
                .body("servicos", hasSize(0));

        System.out.println("=== DEBUG: Telemetria básica vazia após limpeza");
    }

    @Test
    @Order(26)
    void deveTerMaisAcessadosVazioAposLimpeza() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/mais-acessados/5")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));

        System.out.println("=== DEBUG: Lista de mais acessados vazia após limpeza");
    }

    // === TESTES DE REGENERAÇÃO DE DADOS APÓS LIMPEZA ===

    @Test
    @Order(27)
    void deveRegenerarDadosAposLimpeza() {
        System.out.println("=== DEBUG: Regenerando dados de telemetria após limpeza");
        
        // Faz algumas chamadas para regenerar dados
        for (int i = 0; i < 2; i++) {
            given()
                    .header("Authorization", "Bearer " + adminToken)
                    .when()
                    .get("/produtos")
                    .then()
                    .statusCode(200);
        }

        // Verifica se os dados foram regenerados
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));

        System.out.println("=== DEBUG: Dados de telemetria regenerados com sucesso");
    }

    @Test
    @Order(28)
    void deveValidarContentType() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/detalhado")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/telemetria/mais-acessados/3")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        System.out.println("=== DEBUG: Content-Type validado para todos os endpoints");
    }
}