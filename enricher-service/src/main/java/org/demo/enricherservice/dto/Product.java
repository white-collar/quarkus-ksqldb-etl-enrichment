package org.demo.enricherservice.dto;

public class Product {
    public String productId;
    public String title;

    public Product() {
    }

    public Product(String productId, String title) {
        this.productId = productId;
        this.title = title;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
