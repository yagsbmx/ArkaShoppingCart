package com.example.shoppingcart.infraestructure.adapter.out.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private BigDecimal unitPrice;
}

