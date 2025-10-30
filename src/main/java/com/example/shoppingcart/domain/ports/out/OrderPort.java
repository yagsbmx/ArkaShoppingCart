package com.example.shoppingcart.domain.ports.out;

import com.example.shoppingcart.infraestructure.adapter.out.order.OrderFeignRequestDto;

public interface OrderPort {
    void sendOrder(OrderFeignRequestDto orderRequest);
}
