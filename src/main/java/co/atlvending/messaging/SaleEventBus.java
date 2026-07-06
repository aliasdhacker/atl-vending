package co.atlvending.messaging;

import co.atlvending.domain.Sale;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Lab 07 — Reactive Endpoints with Mutiny.
 * An in-memory multicast bus used to stream sales to Server-Sent Event
 * subscribers. Multiple subscribers each receive every event.
 */
@ApplicationScoped
public class SaleEventBus {

    private final BroadcastProcessor<Sale> bus = BroadcastProcessor.create();

    public void publish(Sale s) {
        bus.onNext(s);
    }

    public Multi<Sale> stream() {
        return bus;
    }
}
