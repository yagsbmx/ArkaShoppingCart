package com.example.shoppingcart.infraestructure.adapter.out.product;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.example.shoppingcart.domain.ports.out.ProductPort;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProductFeignAdapter implements ProductPort {

    private final ProductFeignClient productClient;

    @Override
    public boolean isAvailable(Long productId, Integer quantity) {
        try {
            var p = productClient.getById(productId);
            return p != null && p.getStock() != null && p.getStock() >= quantity;
        } catch (feign.FeignException.NotFound e) {
            return false;
        } catch (feign.FeignException e) {
            throw new IllegalStateException("Error calling product-service: " + e.status(), e);
        }
    }

    @Override
    public BigDecimal getPrice(Long productId) {
        try {
            var p = productClient.getById(productId);
            return p != null ? p.getPrice() : BigDecimal.ZERO;
        } catch (feign.FeignException e) {
            throw new IllegalStateException("Error getting product price", e);
        }
    }

    @Override
    public void decrementStock(Long productId, Integer quantity) {
        throw new UnsupportedOperationException("decrementStock not implemented in shoppingcart adapter");
    }

    @Override
    public void incrementStock(Long productId, Integer quantity) {
        throw new UnsupportedOperationException("incrementStock not implemented in shoppingcart adapter");
    }
}