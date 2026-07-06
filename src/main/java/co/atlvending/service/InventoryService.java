package co.atlvending.service;

import co.atlvending.config.VendingConfig;
import co.atlvending.domain.Machine;
import co.atlvending.domain.MachineStatus;
import co.atlvending.messaging.RestockAlert;
import co.atlvending.messaging.SaleEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Lab 05 — Panache persistence for machines.
 * Lab 08 — tracks stock levels so a Kafka sale can trip a restock alert.
 *
 * Stock is kept in-memory keyed by "machineId:sku" to keep the app
 * self-contained; a production build would persist it.
 */
@ApplicationScoped
public class InventoryService {

    @Inject
    VendingConfig config;

    private static final int DEFAULT_STOCK = 8;
    private final ConcurrentMap<String, Integer> stock = new ConcurrentHashMap<>();

    public List<Machine> all() {
        return Machine.listAll();
    }

    public List<Machine> byStatus(MachineStatus status) {
        return Machine.list("status", status);
    }

    public Optional<Machine> find(Long id) {
        return Optional.ofNullable(Machine.findById(id));
    }

    public long count() {
        return Machine.count();
    }

    @Transactional
    public Machine create(Machine m) {
        m.persist();
        return m;
    }

    /**
     * Lab 08 — decrement stock for a sale and, if it crosses the restock
     * threshold, produce a {@link RestockAlert}.
     */
    public Optional<RestockAlert> decrementAndCheck(SaleEvent sale) {
        String k = key(sale.machineId(), sale.sku());
        int remaining = stock.compute(k, (key, cur) -> {
            int start = (cur == null) ? DEFAULT_STOCK : cur;
            return Math.max(0, start - 1);
        });
        if (remaining <= config.restockThreshold()) {
            return Optional.of(new RestockAlert(sale.machineId(), sale.sku(), remaining, Instant.now()));
        }
        return Optional.empty();
    }

    /** Test/demo hook: set a slot's stock level directly. */
    public void setStock(Long machineId, String sku, int quantity) {
        stock.put(key(machineId, sku), quantity);
    }

    private static String key(Long machineId, String sku) {
        return machineId + ":" + sku;
    }
}
