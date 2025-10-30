package com.example.shoppingcart.domain.ports.out;

import java.math.BigDecimal;

public interface ProductStockPort {

    double getAvailableStock(Long productId);
    void decreaseStock(Long productId, int quantity);
    void increaseStock(Long productId, int quantity);
    ProductInfo getProductInfo(Long productId);

    record ProductInfo(Long id, String name, BigDecimal price) {}
}
