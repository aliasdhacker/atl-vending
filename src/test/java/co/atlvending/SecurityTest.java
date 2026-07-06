package co.atlvending;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Lab 09 — JWT / role-based access, exercised with @TestSecurity (no real token).
 */
@QuarkusTest
class SecurityTest {

    private static final String RESTOCK_BODY = "{\"sku\":\"COKE-12\",\"quantity\":5}";

    @Test
    void anonymousIsRejected() {
        given()
                .contentType("application/json").body(RESTOCK_BODY)
                .when().post("/machines/1/restock")
                .then().statusCode(401);
    }

    @Test
    @TestSecurity(user = "alice", roles = "OPERATOR")
    void operatorCanRestock() {
        given()
                .contentType("application/json").body(RESTOCK_BODY)
                .when().post("/machines/1/restock")
                .then().statusCode(204);
    }

    @Test
    @TestSecurity(user = "bob", roles = "VIEWER")
    void wrongRoleIsForbidden() {
        given()
                .contentType("application/json").body(RESTOCK_BODY)
                .when().post("/machines/1/restock")
                .then().statusCode(403);
    }

    @Test
    @TestSecurity(user = "carol", roles = "ADMIN")
    void adminCanDecommission() {
        given()
                .when().delete("/machines/2")
                .then().statusCode(204);
    }
}
