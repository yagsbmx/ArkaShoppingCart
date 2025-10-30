package com.example.shoppingcart.infraestructure.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemRequestDto {
    @NotNull
    private Long productId;

    private String name;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private BigDecimal price;
}
