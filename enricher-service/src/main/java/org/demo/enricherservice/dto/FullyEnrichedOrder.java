package org.demo.enricherservice.dto;

public class FullyEnrichedOrder {
    public EnrichedOrder enriched;
    public Product product;

    public FullyEnrichedOrder() {}
    public FullyEnrichedOrder(EnrichedOrder e, Product p) {
        this.enriched = e;
        this.product = p;
    }

    public EnrichedOrder getEnriched() {
        return enriched;
    }

    public void setEnriched(EnrichedOrder enriched) {
        this.enriched = enriched;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
