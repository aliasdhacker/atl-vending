package co.atlvending;

import co.atlvending.service.InventoryService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

/**
 * Lab 03 — @InjectMock (the Quarkus equivalent of Spring's @MockBean).
 */
@QuarkusTest
class MachineResourceMockTest {

    @InjectMock
    InventoryService inventory;

    @Test
    void returns404WhenMachineMissing() {
        when(inventory.find(999L)).thenReturn(Optional.empty());
        given()
                .when().get("/machines/999")
                .then()
                .statusCode(404);
    }
}
