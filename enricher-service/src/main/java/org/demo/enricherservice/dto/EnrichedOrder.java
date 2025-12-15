package org.demo.enricherservice.dto;

public class EnrichedOrder {
    public Order order;
    public User user;

    public EnrichedOrder() {}

    public EnrichedOrder(Order o, User u) {
        this.order = o;
        this.user = u;
    }

    public String order() {
        return this.order.productId;
    }
}
