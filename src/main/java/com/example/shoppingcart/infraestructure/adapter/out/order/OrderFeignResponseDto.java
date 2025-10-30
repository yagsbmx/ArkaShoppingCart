package com.example.shoppingcart.infraestructure.adapter.out.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFeignResponseDto {
    private Long id;
    private Long clientId;
    private String status; 
    private List<OrderItemResponseDto> items;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
}