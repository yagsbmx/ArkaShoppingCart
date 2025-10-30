package com.example.shoppingcart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    private Long id;
    private Long productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private Cart cart;
}

