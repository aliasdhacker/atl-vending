package co.atlvending.service;

import co.atlvending.api.dto.RestockRequest;
import co.atlvending.config.VendingConfig;
import co.atlvending.domain.Machine;
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
 * Lab 03 — Services + CDI Injection (extracted from the resource layer).
 * Lab 04 — uses {@link VendingConfig} for restock limits.
 * Lab 08 — tracks stock levels so a sale can trip a restock alert.
 *
 * Stock is kept in-memory keyed by "machineId:sku" to keep the reference app
 * self-contained; a production build would persist stock in the database.
 */
@ApplicationScoped
public class InventoryService {

    @Inject
    VendingConfig config;

    private final ConcurrentMap<String, Integer> stock = new ConcurrentHashMap<>();

    public List<Machine> all() {
        return Machine.listAll();
    }

    public List<Machine> byStatus(co.atlvending.domain.MachineStatus status) {
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

    @Transactional
    public boolean decommission(Long id) {
        return Machine.deleteById(id);
    }

    /**
     * Lab 09 — refill a machine slot. Enforces the configured maximum.
     */
    public void restock(Long machineId, RestockRequest req) {
        if (req.quantity() > config.maxRestockQuantity()) {
            throw new IllegalArgumentException(
                    "Restock quantity " + req.quantity() + " exceeds max " + config.maxRestockQuantity());
        }
        stock.merge(key(machineId, req.sku()), req.quantity(), Integer::sum);
    }

    /** Starting stock for a slot the first time it is sold from. */
    private static final int DEFAULT_STOCK = 8;

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
