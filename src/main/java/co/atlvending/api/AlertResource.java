package co.atlvending.api;

import co.atlvending.messaging.RestockAlert;
import co.atlvending.messaging.RestockAlertLogger;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Lab 08 — view restock alerts that have travelled the full Kafka round-trip
 * (POST /sales → "sales" topic → RestockMonitor → "restock-alerts" topic →
 * RestockAlertLogger).
 */
@Path("/restock-alerts")
@Produces(MediaType.APPLICATION_JSON)
public class AlertResource {

    @Inject
    RestockAlertLogger alerts;

    @GET
    public List<RestockAlert> recent() {
        return alerts.recent();
    }
}
