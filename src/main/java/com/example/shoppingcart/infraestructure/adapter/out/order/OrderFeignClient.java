package com.example.shoppingcart.infraestructure.adapter.out.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.MediaType;

@FeignClient(
    name = "order-service",
    path = "/api/orders",
    configuration = OrderFeignConfig.class
)
public interface OrderFeignClient {

    @PostMapping(
        value = "/create",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderFeignResponseDto createOrder(@RequestBody OrderFeignRequestDto request);
}




