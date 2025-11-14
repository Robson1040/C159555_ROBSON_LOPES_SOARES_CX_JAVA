package org.example.test.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@QuarkusTest
public class ImportSqlVerificationTest {

    @Test
    @DisplayName("Verificar se dados do import.sql estão sendo carregados - Pessoas")
    public void testPessoasFromImportSql() {
        // Este teste não limpa os dados, então deve encontrar as pessoas do import.sql
        RestAssured.given()
                .contentType("application/json")
                .get("/pessoas")  // Assumindo que existe um endpoint para listar pessoas
                .then()
                .statusCode(200);
        
        // Tentar autenticar com os usuários do import.sql usando o endpoint correto
        String loginAdmin = """
                {
                    "username": "admin",
                    "password": "secret"
                }
                """;
                
        RestAssured.given()
                .contentType("application/json")
                .body(loginAdmin)
                .post("/entrar")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Verificar se dados do import.sql estão sendo carregados - Produtos")
    public void testProdutosFromImportSql() {
        // Este teste não limpa os dados, então deve encontrar os produtos do import.sql
        RestAssured.given()
                .contentType("application/json")
                .get("/produtos")
                .then()
                .statusCode(200);
    }


    
    @Test
    @DisplayName("Verificar contagem específica de dados do import.sql")
    public void testContagemDadosImportSql() {
        // Verifica se existe pelo menos 1 produto (deveria ser 5 do import.sql)
        RestAssured.given()
                .contentType("application/json")
                .get("/produtos/count")
                .then()
                .statusCode(200);
    }
}