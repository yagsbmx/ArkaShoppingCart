package com.example.shoppingcart.infraestructure.adapter.out.user;

import com.example.shoppingcart.infraestructure.config.FeignAuthForwardConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        path = "/api/users",
        configuration = FeignAuthForwardConfig.class
)
public interface UserFeignClient {

    @GetMapping("/{id}")
    UserFeignResponseDto getUserById(@PathVariable("id") Long id);
}
