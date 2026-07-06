package co.atlvending.messaging;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

/** JSON deserializer binding for {@link RestockAlert} on the "restock-alerts" topic. */
public class RestockAlertDeserializer extends ObjectMapperDeserializer<RestockAlert> {
    public RestockAlertDeserializer() {
        super(RestockAlert.class);
    }
}
