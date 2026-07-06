package co.atlvending.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Lab 02 — a product, with Bean Validation constraints. Modelled as a record
 * for now; becomes a Panache entity in Lab 05.
 */
public record Product(
        @NotBlank String sku,
        @NotBlank String name,
        @Positive int priceCents) {
}
