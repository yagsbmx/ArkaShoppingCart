package com.example.shoppingcart.infraestructure.adapter.out.product;

import com.example.shoppingcart.infraestructure.config.FeignAuthForwardConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        path = "/api/products",
        configuration = FeignAuthForwardConfig.class
)
public interface ProductFeignClient {

    @GetMapping("/{id}")
    ProductFeignResponseDto getById(@PathVariable("id") Long id);
}
