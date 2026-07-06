package co.atlvending.messaging;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Lab 08 — consumes the "restock-alerts" topic. Logs each alert and keeps the
 * most recent ones in memory so they can be viewed at GET /restock-alerts
 * (see {@code AlertResource}) — handy for demoing the full Kafka round-trip.
 */
@ApplicationScoped
public class RestockAlertLogger {

    private static final int MAX_KEPT = 50;
    private final ConcurrentLinkedDeque<RestockAlert> recent = new ConcurrentLinkedDeque<>();

    @Incoming("restock-in")
    public void onAlert(RestockAlert alert) {
        Log.warnf("RESTOCK NEEDED — machine %d, sku %s, only %d left",
                alert.machineId(), alert.sku(), alert.remaining());
        recent.addFirst(alert);
        while (recent.size() > MAX_KEPT) {
            recent.pollLast();
        }
    }

    public List<RestockAlert> recent() {
        return List.copyOf(recent);
    }
}
