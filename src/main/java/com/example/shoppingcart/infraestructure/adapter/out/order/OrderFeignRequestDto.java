package com.example.shoppingcart.infraestructure.adapter.out.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFeignRequestDto {

    @NotNull
    private Long clientId;

    @NotEmpty
    private List<OrderItemRequestDto> items;

}

