package co.atlvending.health;

import co.atlvending.service.InventoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Lab 10 — Observability. Reports the app as ready only if the inventory
 * store is reachable.
 */
@Readiness
@ApplicationScoped
public class InventoryReadiness implements HealthCheck {

    @Inject
    InventoryService inventory;

    @Override
    public HealthCheckResponse call() {
        try {
            long count = inventory.count();
            return HealthCheckResponse.named("inventory")
                    .up()
                    .withData("machines", count)
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("inventory").down().build();
        }
    }
}
