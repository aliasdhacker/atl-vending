package co.atlvending.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Lab 02 — validated input for POST /products.
 */
public record ProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @Positive int priceCents) {
}
