package com.example.shoppingcart.infraestructure.adapter.out.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/users/{id}")
    UserFeignResponseDto getUserById(@PathVariable("id") Long id);
}