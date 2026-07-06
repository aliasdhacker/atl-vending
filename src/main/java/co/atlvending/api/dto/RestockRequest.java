package co.atlvending.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Lab 09 — payload for POST /machines/{id}/restock.
 */
public record RestockRequest(
        @NotBlank String sku,
        @Positive int quantity) {
}
