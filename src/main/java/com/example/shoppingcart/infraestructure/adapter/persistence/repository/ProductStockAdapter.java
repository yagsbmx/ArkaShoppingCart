package com.example.shoppingcart.infraestructure.adapter.persistence.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.example.shoppingcart.domain.ports.out.ProductStockPort;
import java.math.BigDecimal;

@Component
public class ProductStockAdapter implements ProductStockPort {

    private static final Logger log = LoggerFactory.getLogger(ProductStockAdapter.class);

    @Override
    public double getAvailableStock(Long productId) {
        log.debug("Consultando stock disponible para producto {} → 100 unidades (simulado)", productId);
        return 100;
    }

    @Override
    public void decreaseStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a disminuir debe ser mayor que cero");
        }
        log.info("Stock del producto {} disminuido en {} unidades. (simulado)", productId, quantity);
    }

    @Override
    public void increaseStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor que cero");
        }
        log.info("Stock del producto {} incrementado en {} unidades. (simulado)", productId, quantity);
    }

    @Override
    public ProductInfo getProductInfo(Long productId) {
        String productName = "Producto genérico " + productId;
        BigDecimal productPrice = BigDecimal.valueOf(29.99);
        log.debug("Información del producto {} → {} ({})", productId, productName, productPrice);
        return new ProductStockPort.ProductInfo(productId, productName, productPrice);
    }
}