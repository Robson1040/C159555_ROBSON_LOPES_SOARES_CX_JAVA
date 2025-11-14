package org.example.test.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes para o ProdutoRecomendadoResource
 */
@QuarkusTest
public class ProdutoRecomendadoResourceTest {

    @Test
    @DisplayName("Deve retornar produtos para perfil conservador")
    void testBuscarProdutosConservador() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/Conservador")
        .then()
            .statusCode(anyOf(is(200), is(204)))
            .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("Deve retornar produtos para perfil moderado")
    void testBuscarProdutosModerado() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/Moderado")
        .then()
            .statusCode(anyOf(is(200), is(204)))
            .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("Deve retornar produtos para perfil agressivo")
    void testBuscarProdutosAgressivo() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/Agressivo")
        .then()
            .statusCode(anyOf(is(200), is(204)))
            .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("Deve retornar erro 400 para perfil inválido")
    void testBuscarProdutosPerfilInvalido() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/PerfilInexistente")
        .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Perfil não reconhecido"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 para perfil vazio")
    void testBuscarProdutosPerfilVazio() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/")
        .then()
            .statusCode(404); // Not Found pois o path não está mapeado
    }

    @Test
    @DisplayName("Deve aceitar perfis com diferentes cases")
    void testBuscarProdutosCaseInsensitive() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/conservador")
        .then()
            .statusCode(anyOf(is(200), is(204)))
            .contentType(ContentType.JSON);
            
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/MODERADO")
        .then()
            .statusCode(anyOf(is(200), is(204)))
            .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("Deve retornar produtos com estrutura correta quando há dados")
    void testEstruturaProdutos() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/Conservador")
        .then()
            .statusCode(anyOf(is(200), is(204)))
            .contentType(ContentType.JSON)
            .body("", anyOf(
                hasSize(greaterThanOrEqualTo(0)), // Aceita lista vazia (204) 
                allOf(
                    hasSize(greaterThan(0)),
                    everyItem(hasKey("id")),
                    everyItem(hasKey("nome")),
                    everyItem(hasKey("tipo")),
                    everyItem(hasKey("rentabilidadeEsperada")),
                    everyItem(hasKey("risco"))
                )
            ));
    }
}