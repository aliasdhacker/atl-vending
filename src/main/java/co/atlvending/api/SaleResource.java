package co.atlvending.api;

import co.atlvending.domain.Sale;
import co.atlvending.messaging.SaleEvent;
import co.atlvending.messaging.SaleEventBus;
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
 * Lab 07 — reactive SSE feed.
 * Lab 08 — also emits a Kafka SaleEvent for the RestockMonitor.
 * (Lab 10 adds metrics.)
 */
@Path("/sales")
public class SaleResource {

    @Inject
    SaleEventBus events;

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

        return Response.status(Response.Status.CREATED).entity(sale).build();
    }

    @GET
    @Path("/feed")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Sale> liveFeed() {
        return events.stream();
    }
}
