package org.demo;

import jakarta.enterprise.context.ApplicationScoped;
import org.demo.dto.EnrichedOrder;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class EnrichedOrderConsumer {

    @Incoming("orders_enriched")
    public void consume(EnrichedOrder order) {
        System.out.println("Consumed enriched order: " + order);
    }
}
