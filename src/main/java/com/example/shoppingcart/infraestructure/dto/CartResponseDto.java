package com.example.shoppingcart.infraestructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.shoppingcart.domain.model.enums.CartStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {

    private Long id;
    private Long userId;
    private CartStatus status;
    private List<CartItemResponseDto> items;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

