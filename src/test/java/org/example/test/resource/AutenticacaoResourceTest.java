package org.example.test.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.model.Pessoa;
import org.example.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class AutenticacaoResourceTest {

    @Inject
    PasswordService passwordService;

    @BeforeEach
    @Transactional
    public void setup() {
        // Limpar dados existentes
        Pessoa.deleteAll();
        
        // Criar usuário de teste com H2
        Pessoa usuario = new Pessoa();
        usuario.nome = "João Teste";
        usuario.cpf = "12345678901";
        usuario.username = "joao.teste";
        usuario.password = passwordService.encryptPassword("senha123");
        usuario.role = "USER";
        usuario.persist();
    }

    @Test
    public void testLoginComCredenciaisValidas() {
        String loginPayload = """
            {
                "username": "joao.teste",
                "password": "senha123"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(loginPayload)
            .when()
            .post("/entrar")
            .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("tipo", equalTo("Bearer"))
            .body("usuario", equalTo("joao.teste"))
            .body("role", equalTo("USER"))
            .body("expira_em", notNullValue());
    }

    @Test
    public void testLoginComCredenciaisInvalidas() {
        String loginPayload = """
            {
                "username": "joao.teste",
                "password": "senhaerrada"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(loginPayload)
            .when()
            .post("/entrar")
            .then()
            .statusCode(401)
            .body("message", equalTo("Credenciais inválidas"))
            .body("status", equalTo(401));
    }

    @Test
    public void testLoginComUsuarioInexistente() {
        String loginPayload = """
            {
                "username": "usuario.inexistente",
                "password": "qualquersenha"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(loginPayload)
            .when()
            .post("/entrar")
            .then()
            .statusCode(401)
            .body("message", equalTo("Credenciais inválidas"))
            .body("status", equalTo(401));
    }

    @Test
    public void testLoginComDadosVazios() {
        String loginPayload = """
            {
                "username": "",
                "password": ""
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(loginPayload)
            .when()
            .post("/entrar")
            .then()
            .statusCode(400);
    }

    @Test
    public void testTokenJwtContemInformacoesCorretas() {
        String loginPayload = """
            {
                "username": "joao.teste",
                "password": "senha123"
            }
            """;

        String response = given()
            .contentType(ContentType.JSON)
            .body(loginPayload)
            .when()
            .post("/entrar")
            .then()
            .statusCode(200)
            .extract()
            .path("token");

        // Verificar se o token tem 3 partes (header.payload.signature)
        String[] tokenParts = response.split("\\.");
        assert tokenParts.length == 3 : "Token JWT deve ter 3 partes separadas por ponto";
        
        // Decodificar payload (base64) e verificar conteúdo básico
        java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
        String payload = new String(decoder.decode(tokenParts[1]));
        
        assert payload.contains("joao.teste") : "Token deve conter o username";
        assert payload.contains("USER") : "Token deve conter a role";
        assert payload.contains("João Teste") : "Token deve conter o nome";
    }
}