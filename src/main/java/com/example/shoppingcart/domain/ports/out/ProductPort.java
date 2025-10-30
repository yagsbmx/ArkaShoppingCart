package com.example.shoppingcart.domain.ports.out;

import java.math.BigDecimal;

public interface ProductPort {
    boolean isAvailable(Long productId, Integer quantity);
    BigDecimal getPrice(Long productId);
    void decrementStock(Long productId, Integer quantity);
    void incrementStock(Long productId, Integer quantity);
}
