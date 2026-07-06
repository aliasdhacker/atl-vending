package co.atlvending.api;

import co.atlvending.domain.Sale;
import co.atlvending.messaging.SaleEvent;
import co.atlvending.messaging.SaleEventBus;
import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.Instant;

/**
 * Labs 07, 08, 10 — recording sales.
 *   POST /sales        record a sale: persist, publish to the live feed,
 *                      emit a Kafka SaleEvent, and bump a metric.
 *   GET  /sales/feed   live Server-Sent Events stream of sales (Mutiny Multi).
 */
@Path("/sales")
public class SaleResource {

    @Inject
    SaleEventBus events;

    @Inject
    MeterRegistry registry;

    @Channel("sales-out")
    Emitter<SaleEvent> salesOut;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response recordSale(Sale sale) {
        sale.occurredAt = Instant.now();
        sale.persist();

        // Lab 07 — fan out to live SSE subscribers.
        events.publish(sale);

        // Lab 08 — emit to Kafka for the RestockMonitor.
        salesOut.send(new SaleEvent(sale.machineId, sale.productSku, sale.priceCents, sale.occurredAt));

        // Lab 10 — custom metrics.
        registry.counter("sales.recorded",
                "machine", String.valueOf(sale.machineId),
                "sku", sale.productSku).increment();
        registry.summary("sales.cents").record(sale.priceCents);

        return Response.status(Response.Status.CREATED).entity(sale).build();
    }

    @GET
    @Path("/feed")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Sale> liveFeed() {
        return events.stream();
    }
}
