package co.atlvending.api;

import co.atlvending.domain.Sale;
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

import java.time.Instant;

/**
 * Lab 07 — Reactive endpoints with Mutiny.
 *   POST /sales        record a sale and publish it to the live feed.
 *   GET  /sales/feed   live Server-Sent Events stream of sales.
 * (Lab 08 adds Kafka; Lab 10 adds metrics.)
 */
@Path("/sales")
public class SaleResource {

    @Inject
    SaleEventBus events;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response recordSale(Sale sale) {
        sale.occurredAt = Instant.now();
        sale.persist();
        events.publish(sale);
        return Response.status(Response.Status.CREATED).entity(sale).build();
    }

    @GET
    @Path("/feed")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Sale> liveFeed() {
        return events.stream();
    }
}
