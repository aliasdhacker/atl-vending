package co.atlvending.messaging;

import java.time.Instant;

/**
 * Lab 08 — event emitted to the "sales" Kafka topic when a sale is recorded.
 */
public record SaleEvent(Long machineId, String sku, int priceCents, Instant at) {
}
