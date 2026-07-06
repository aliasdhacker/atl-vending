package co.atlvending.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Lab 04 — Configuration + Profiles.
 * Type-safe configuration for Atlanta Vending Co., bound to the
 * {@code atl.vending.*} property namespace.
 */
@ConfigMapping(prefix = "atl.vending")
public interface VendingConfig {

    @WithDefault("100")
    int maxRestockQuantity();

    @WithDefault("USD")
    String currency();

    /** Stock level at or below which a restock alert is raised (Lab 08). */
    @WithDefault("5")
    int restockThreshold();
}
