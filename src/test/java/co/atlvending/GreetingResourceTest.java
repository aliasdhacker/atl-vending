package co.atlvending;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class GreetingResourceTest {
    @Test
    void welcomeEndpoint() {
        given()
          .when().get("/welcome")
          .then()
             .statusCode(200)
             .body(containsString("Atlanta Vending"));
    }

}