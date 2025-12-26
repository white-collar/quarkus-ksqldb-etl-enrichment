package org.demo;

import jakarta.enterprise.context.ApplicationScoped;
import org.demo.dto.EnrichedOrder;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class EnrichedOrderConsumer {

    private final List<EnrichedOrder> orders = new CopyOnWriteArrayList<>();

    @Incoming("orders-enriched")
    public void consume(EnrichedOrder order) {
        System.out.println("Consumed enriched order: " + order);
        orders.add(order);
    }

    public List<EnrichedOrder> getOrders() {
        return orders;
    }
}
