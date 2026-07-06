package co.atlvending.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Lab 03 — a second CDI bean. Computes an order total from line items.
 */
@ApplicationScoped
public class PricingService {

    public record SaleItem(String sku, int priceCents, int quantity) {
    }

    public int calculateTotal(List<SaleItem> items) {
        return items.stream()
                .mapToInt(i -> i.priceCents() * i.quantity())
                .sum();
    }
}
