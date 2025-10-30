package com.example.shoppingcart.infraestructure.adapter.out.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long productId;
    private Integer quantity;
    private String name;
}