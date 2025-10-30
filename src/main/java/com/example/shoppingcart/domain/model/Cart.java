package com.example.shoppingcart.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.shoppingcart.domain.model.enums.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    private Long id;
    private Long userId;
    private CartStatus status;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

