package org.demo.enricherservice.dto;

public class FullyEnrichedOrder {
    public EnrichedOrder enriched;
    public Product product;

    public FullyEnrichedOrder() {}
    public FullyEnrichedOrder(EnrichedOrder e, Product p) {
        this.enriched = e;
        this.product = p;
    }

    @Override
    public String toString() {
        return "FullyEnrichedOrder{" +
                "order=" + enriched.order +
                ", user=" + enriched.user +
                ", product=" + product +
                '}';
    }
}
