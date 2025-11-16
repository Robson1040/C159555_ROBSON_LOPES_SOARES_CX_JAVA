package br.gov.caixa.api.investimentos.resource.autenticacao;

import br.gov.caixa.api.investimentos.dto.autenticacao.LoginRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

@QuarkusTest
class AutenticacaoTokenClaimTest {
    static class ClienteData {
        String nome;
        String cpf;
        String email;
        String senha;
        Long id;
        ClienteData(String nome, String cpf, String email, String senha) {
            this.nome = nome;
            this.cpf = cpf;
            this.email = email;
            this.senha = senha;
        }
    }

    @Test
    void deveValidarUserIdNoTokenParaCadaCliente() {
        List<ClienteData> clientes = List.of(
            new ClienteData("Joao Teste", "93645920005", "joao1@teste.com", "senha1"),
            new ClienteData("Maria Teste", "58696742044", "maria2@teste.com", "senha2"),
            new ClienteData("Carlos Teste", "11091489092", "carlos3@teste.com", "senha3")
        );

        for (ClienteData cliente : clientes) {
            // Cadastra o cliente
            Map<String, Object> clienteRequest = Map.of(
                "nome", cliente.nome,
                "cpf", cliente.cpf,
                "email", cliente.email,
                "password", cliente.senha,
                "role", "USER",
                "username", cliente.email
            );
            cliente.id = ((Number) given()
                .contentType(ContentType.JSON)
                .body(clienteRequest)
                .when()
                .post("/clientes")
                .then()
                .statusCode(201)
                .extract()
                .path("id")).longValue();

            // Faz login e obtém token
            LoginRequest loginRequest = new LoginRequest(cliente.email, cliente.senha);
            String token = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/entrar")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

            // Decodifica o token JWT (simples, sem validação de assinatura)
            String[] parts = token.split("\\.");
            assertEquals(3, parts.length, "Token JWT deve ter 3 partes");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            assertTrue(payload.contains("userId"), "Payload deve conter claim userId");
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\"userId\":(\\d+)").matcher(payload);
            assertTrue(matcher.find(), "Payload deve conter claim userId como número");
            String userIdStr = matcher.group(1);
            assertEquals(cliente.id.toString(), userIdStr, "Claim userId deve ser igual ao id cadastrado");
        }
    }
}
