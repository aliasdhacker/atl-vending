package co.atlvending;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Labs 02/06 — REST Assured against the seeded machine data.
 */
@QuarkusTest
class MachineResourceTest {

    @Test
    void listMachines() {
        given()
                .when().get("/machines")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].location", notNullValue());
    }

    @Test
    void missingMachineReturns404() {
        given()
                .when().get("/machines/99999")
                .then()
                .statusCode(404)
                .body("error", containsString("99999"));
    }

    @Test
    void filterByStatus() {
        given()
                .when().get("/machines?status=OFFLINE")
                .then()
                .statusCode(200)
                .body("status", org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo("OFFLINE")));
    }
}
