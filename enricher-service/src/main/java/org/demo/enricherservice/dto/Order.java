package org.demo.enricherservice.dto;

public class Order {
    public String orderId;
    public String userId;
    public String productId;

    public Order(String orderId, String userId, String productId) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
    }

    public Order() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
