package org.demo.dto;


import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class EnrichedOrderDeserializer extends ObjectMapperDeserializer<EnrichedOrder> {
    public EnrichedOrderDeserializer() {
        super(EnrichedOrder.class);
    }
}
