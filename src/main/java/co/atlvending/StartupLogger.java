package co.atlvending;

import co.atlvending.config.VendingConfig;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

/**
 * Lab 04 — proves which configuration profile is active at startup.
 */
@ApplicationScoped
public class StartupLogger {

    @Inject
    VendingConfig config;

    void onStart(@Observes StartupEvent ev) {
        Log.infof("Atlanta Vending Co. starting — profiles: %s, max restock: %d, currency: %s",
                ConfigUtils.getProfiles(),
                config.maxRestockQuantity(),
                config.currency());
    }
}
