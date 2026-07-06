package co.atlvending.messaging;

import java.time.Instant;

/**
 * Lab 08 — alert emitted to the "restock-alerts" topic when stock is low.
 */
public record RestockAlert(Long machineId, String sku, int remaining, Instant at) {
}
