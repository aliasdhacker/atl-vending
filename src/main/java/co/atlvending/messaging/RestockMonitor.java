package co.atlvending.messaging;

import co.atlvending.service.InventoryService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.util.Optional;

/**
 * Lab 08 — Kafka Sale Events.
 * Consumes the "sales" topic, decrements stock, and emits a RestockAlert to
 * the "restock-alerts" topic whenever a sale takes stock below the threshold.
 */
@ApplicationScoped
public class RestockMonitor {

    @Inject
    InventoryService inventory;

    @Incoming("sales-in")
    @Outgoing("restock-out")
    public Multi<RestockAlert> onSale(Multi<SaleEvent> sales) {
        return sales
                .map(inventory::decrementAndCheck)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
}
