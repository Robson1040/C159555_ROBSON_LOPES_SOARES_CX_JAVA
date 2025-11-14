package org.example.test.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
public class TelemetriaResourceTest {

    @Test
    public void testTelemetriaEndpoint() {
        given()
            .when()
                .get("/telemetria")
            .then()
                .statusCode(200)
                .body("servicos", notNullValue())
                .body("periodo", notNullValue())
                .body("servicos.size()", greaterThan(0))
                .body("periodo.inicio", notNullValue())
                .body("periodo.fim", notNullValue());
    }

    @Test
    public void testTelemetriaEndpointResponseFormat() {
        given()
            .when()
                .get("/telemetria")
            .then()
                .statusCode(200)
                .body("servicos[0].nome", notNullValue())
                .body("servicos[0].quantidadeChamadas", greaterThanOrEqualTo(0))
                .body("servicos[0].mediaTempoRespostaMs", greaterThanOrEqualTo(0f));
    }
}