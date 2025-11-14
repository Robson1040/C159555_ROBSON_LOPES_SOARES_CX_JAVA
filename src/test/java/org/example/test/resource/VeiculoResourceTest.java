package org.example.test.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.example.dto.AtualizaStatusVeiculoRequest;
import org.example.dto.VeiculoRequest;
import org.example.model.Veiculo;
import org.example.model.VeiculoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class VeiculoResourceTest
{
    static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Buscar todos deve retornar 200")
    public void test1() throws JsonProcessingException
    {
        RestAssured.given()
                .contentType("application/json")
                .get("/veiculos")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("404 - veiculo não encontrado")
    public void test2() throws JsonProcessingException
    {
        RestAssured.given()
                .contentType("application/json")
                .get("/veiculos/365")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Criar veiculo com algum campo null")
    public void test3() throws JsonProcessingException
    {
        VeiculoRequest b = new VeiculoRequest(null, null, 0, null);

        String body = objectMapper.writeValueAsString(b);

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .post("/veiculos")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Criar veiculo com algum campo vazio")
    public void test4() throws JsonProcessingException
    {
        VeiculoRequest b = new VeiculoRequest("", "", 0, "");

        String body = objectMapper.writeValueAsString(b);

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .post("/veiculos")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Mudar o status de `UNDER_MAINTENANCE` para `RENTED` deve retornar o status code 409")
    public void test5() throws JsonProcessingException
    {
        Veiculo veiculo = criarVeiculoManutencao();

        AtualizaStatusVeiculoRequest b = new AtualizaStatusVeiculoRequest(VeiculoStatus.RENTED);

        String body = objectMapper.writeValueAsString(b);

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .patch("/veiculos/" + veiculo.getId())
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Criar Veiculo")
    public void test6() throws JsonProcessingException
    {
        VeiculoRequest b = new VeiculoRequest("Fiat", "Mobi", 2020, "1.0");

        String body = objectMapper.writeValueAsString(b);

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .post("/veiculos")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Alterar para Rented")
    public void test7() throws JsonProcessingException
    {
        Veiculo v = criarVeiculo();

        AtualizaStatusVeiculoRequest b = new AtualizaStatusVeiculoRequest(VeiculoStatus.RENTED);

        String body = objectMapper.writeValueAsString(b);

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .patch("/veiculos/" + v.getId())
                .then()
                .statusCode(204);
    }

    @Transactional
    public Veiculo criarVeiculo()
    {
        Veiculo v = new Veiculo("Fiat", "Mobi", 2020, "1.0");
        v.persist();

        return v;
    }

    @Transactional
    public Veiculo criarVeiculoManutencao()
    {
        Veiculo v = new Veiculo("Fiat", "Mobi", 2020, "1.0");
        v.atualizaStatus(VeiculoStatus.UNDER_MAINTENANCE);
        v.persist();

        return v;
    }
}
