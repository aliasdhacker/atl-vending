package co.atlvending;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Lab 02 — Bean Validation on POST /products.
 */
@QuarkusTest
class ProductValidationTest {

    @Test
    void rejectsInvalidProduct() {
        given()
                .contentType("application/json")
                .body("{\"sku\":\"\",\"name\":\"\",\"priceCents\":-5}")
                .when().post("/products")
                .then()
                .statusCode(400);
    }

    @Test
    void acceptsValidProduct() {
        given()
                .contentType("application/json")
                .body("{\"sku\":\"TEST-1\",\"name\":\"Test Bar\",\"priceCents\":199}")
                .when().post("/products")
                .then()
                .statusCode(201);
    }
}
