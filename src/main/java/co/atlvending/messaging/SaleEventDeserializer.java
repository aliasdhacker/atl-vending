package co.atlvending.messaging;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

/** JSON deserializer binding for {@link SaleEvent} on the "sales" topic. */
public class SaleEventDeserializer extends ObjectMapperDeserializer<SaleEvent> {
    public SaleEventDeserializer() {
        super(SaleEvent.class);
    }
}
