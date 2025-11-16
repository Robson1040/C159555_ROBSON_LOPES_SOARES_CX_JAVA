package br.gov.caixa.api.investimentos.resource.cliente;

import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteUpdateRequest;
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
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para todos os endpoints da ClienteResource.
 * 
 * Os testes seguem uma ordem lógica:
 * 1. Criar cliente com role USER
 * 2. Alterar username do cliente
 * 3. Consultar cliente por CPF
 * 4. Consultar cliente por username
 * 5. Consultar cliente por ID
 * 6. Consultar todos os clientes
 * 7. Testes de casos de erro
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class ClienteResourceIntegrationTest {

    @Inject
    JwtService jwtService;

    private static Long clienteIdCriado;
    private static String clienteCpfCriado;
    private static String clienteUsernameAtualizado = "joao.silva.updated";

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Criar tokens JWT para testes
        // Token para usuário comum (será usado depois que o cliente for criado)
        userToken = jwtService.gerarToken("user@test.com", "USER");
        
        // Token para admin
        adminToken = jwtService.gerarToken("admin@test.com", "ADMIN");
        
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @Order(1)
    void deveIncluirClienteComRoleUser() {
        // Dados do cliente a ser criado
        ClienteRequest clienteRequest = new ClienteRequest(
                "João da Silva",
                "12345678909", // CPF válido
                "joao.silva",
                "senha123456",
                "USER"
        );

        // Fazer requisição POST para criar cliente
        ClienteResponse clienteResponse = given()
                .contentType(ContentType.JSON)
                .body(clienteRequest)
                .when()
                .post("/clientes")
                .then()
                .statusCode(201)
                .body("nome", equalTo("João da Silva"))
                .body("cpf", equalTo("12345678909"))
                .body("username", equalTo("joao.silva"))
                .body("role", equalTo("USER"))
                .body("id", notNullValue())
                .extract()
                .as(ClienteResponse.class);

        // Armazenar dados para próximos testes
        clienteIdCriado = clienteResponse.id();
        clienteCpfCriado = clienteResponse.cpf();
        
        assertNotNull(clienteIdCriado);
        assertEquals("João da Silva", clienteResponse.nome());
        assertEquals("12345678909", clienteResponse.cpf());
        assertEquals("joao.silva", clienteResponse.username());
        assertEquals("USER", clienteResponse.role());
    }

    @Test
    @Order(2)
    void deveAlterarUsernameDoCliente() {
        assertNotNull(clienteIdCriado, "Cliente deve ter sido criado no teste anterior");
        
        String novoUsername = "joao.silva.updated";
        ClienteUpdateRequest updateRequest = new ClienteUpdateRequest(
                null, // não alterar nome
                novoUsername,
                null // não alterar senha
        );

        // Criar token específico para o cliente criado (simulando que esse é o token do cliente)
        // Usando admin token para esta operação pois preciso incluir userId no token
        String clienteToken = adminToken;

        // Fazer requisição PUT para atualizar cliente
        ClienteResponse clienteResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clienteToken)
                .body(updateRequest)
                .when()
                .put("/clientes/" + clienteIdCriado)
                .then()
                .statusCode(200)
                .body("id", equalTo(clienteIdCriado.intValue()))
                .body("nome", equalTo("João da Silva"))
                .body("cpf", equalTo("12345678909"))
                .body("username", equalTo(novoUsername))
                .body("role", equalTo("USER"))
                .extract()
                .as(ClienteResponse.class);

        // Atualizar username para próximos testes
        clienteUsernameAtualizado = clienteResponse.username();
        
        assertEquals(novoUsername, clienteResponse.username());
    }

    @Test
    @Order(3)
    void deveConsultarClientePorCpf() {
        assertNotNull(clienteCpfCriado, "Cliente deve ter sido criado no teste anterior");

        // Fazer requisição GET para buscar por CPF (apenas ADMIN pode fazer isso)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/clientes/cpf/" + clienteCpfCriado)
                .then()
                .statusCode(200)
                .body("id", equalTo(clienteIdCriado.intValue()))
                .body("nome", equalTo("João da Silva"))
                .body("cpf", equalTo(clienteCpfCriado))
                .body("username", equalTo(clienteUsernameAtualizado))
                .body("role", equalTo("USER"));
    }

    @Test
    @Order(4)
    void deveConsultarClientePorUsername() {
        assertNotNull(clienteUsernameAtualizado, "Cliente deve ter sido atualizado no teste anterior");

        // Fazer requisição GET para buscar por username (apenas ADMIN pode fazer isso)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/clientes/username/" + clienteUsernameAtualizado)
                .then()
                .statusCode(200)
                .body("id", equalTo(clienteIdCriado.intValue()))
                .body("nome", equalTo("João da Silva"))
                .body("cpf", equalTo(clienteCpfCriado))
                .body("username", equalTo(clienteUsernameAtualizado))
                .body("role", equalTo("USER"));
    }

    @Test
    @Order(5)
    void deveConsultarClientePorId() {
        assertNotNull(clienteIdCriado, "Cliente deve ter sido criado no teste anterior");

        // Usar token de admin para esta consulta (simulando que o cliente pode ver seus próprios dados)
        String clienteToken = adminToken;

        // Fazer requisição GET para buscar por ID (cliente pode buscar seus próprios dados)
        given()
                .header("Authorization", "Bearer " + clienteToken)
                .when()
                .get("/clientes/" + clienteIdCriado)
                .then()
                .statusCode(200)
                .body("id", equalTo(clienteIdCriado.intValue()))
                .body("nome", equalTo("João da Silva"))
                .body("cpf", equalTo(clienteCpfCriado))
                .body("username", equalTo(clienteUsernameAtualizado))
                .body("role", equalTo("USER"));
    }

    @Test
    @Order(6)
    void deveConsultarTodosOsClientes() {
        // Fazer requisição GET para listar todos os clientes (apenas ADMIN pode fazer isso)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/clientes")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("find { it.id == " + clienteIdCriado + " }.nome", equalTo("João da Silva"))
                .body("find { it.id == " + clienteIdCriado + " }.cpf", equalTo(clienteCpfCriado))
                .body("find { it.id == " + clienteIdCriado + " }.username", equalTo(clienteUsernameAtualizado))
                .body("find { it.id == " + clienteIdCriado + " }.role", equalTo("USER"));
    }

    // Testes de casos de erro

    @Test
    @Order(7)
    void deveRetornar400ParaClienteComDadosInvalidos() {
        // Tentar criar cliente com dados inválidos
        ClienteRequest clienteInvalido = new ClienteRequest(
                "", // nome em branco
                "123", // CPF inválido
                "ab", // username muito curto
                "123", // senha muito curta
                "INVALID_ROLE" // role inválida
        );

        given()
                .contentType(ContentType.JSON)
                .body(clienteInvalido)
                .when()
                .post("/clientes")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(8)
    void deveRetornar404ParaClienteNaoEncontradoPorId() {
        // Tentar buscar cliente inexistente por ID
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/clientes/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    void deveRetornar404ParaClienteNaoEncontradoPorCpf() {
        // Tentar buscar cliente inexistente por CPF
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/clientes/cpf/99999999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(10)
    void deveRetornar404ParaClienteNaoEncontradoPorUsername() {
        // Tentar buscar cliente inexistente por username
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/clientes/username/usuario.inexistente")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(11)
    void deveRetornar403ParaUserTentandoAcessarDadosDeOutroCliente() {
        assertNotNull(clienteIdCriado, "Cliente deve ter sido criado no teste anterior");
        
        // Criar token para outro usuário
        String outroUserToken = jwtService.gerarToken("outro.user@test.com", "USER");

        // Tentar acessar dados de outro cliente
        given()
                .header("Authorization", "Bearer " + outroUserToken)
                .when()
                .get("/clientes/" + clienteIdCriado)
                .then()
                .statusCode(403);
    }

    @Test
    @Order(12)
    void deveRetornar403ParaUserTentandoListarTodosClientes() {
        // Tentar listar todos os clientes com role USER
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/clientes")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(13)
    void deveRetornar403ParaUserTentandoBuscarPorCpf() {
        assertNotNull(clienteCpfCriado, "Cliente deve ter sido criado no teste anterior");
        
        // Tentar buscar por CPF com role USER
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/clientes/cpf/" + clienteCpfCriado)
                .then()
                .statusCode(403);
    }

    @Test
    @Order(14)
    void deveRetornar403ParaUserTentandoBuscarPorUsername() {
        assertNotNull(clienteUsernameAtualizado, "Cliente deve ter sido atualizado no teste anterior");
        
        // Tentar buscar por username com role USER
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/clientes/username/" + clienteUsernameAtualizado)
                .then()
                .statusCode(403);
    }

    @Test
    @Order(15)
    void deveRetornar401ParaRequisicaoSemToken() {
        assertNotNull(clienteIdCriado, "Cliente deve ter sido criado no teste anterior");
        
        // Tentar fazer requisição sem token de autorização
        given()
                .when()
                .get("/clientes/" + clienteIdCriado)
                .then()
                .statusCode(401);
    }
}