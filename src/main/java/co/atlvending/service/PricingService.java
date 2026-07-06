package co.atlvending.service;

import co.atlvending.config.VendingConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Lab 03 — a second CDI bean, injected alongside {@link InventoryService}.
 * Computes an order total from a list of line items.
 */
@ApplicationScoped
public class PricingService {

    @Inject
    VendingConfig config;

    public record SaleItem(String sku, int priceCents, int quantity) {
    }

    public int calculateTotal(List<SaleItem> items) {
        return items.stream()
                .mapToInt(i -> i.priceCents() * i.quantity())
                .sum();
    }

    public String currency() {
        return config.currency();
    }
}
