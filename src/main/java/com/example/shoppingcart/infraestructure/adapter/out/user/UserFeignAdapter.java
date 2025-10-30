package com.example.shoppingcart.infraestructure.adapter.out.user;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.example.shoppingcart.domain.ports.out.UserPort;

@Component
@RequiredArgsConstructor
public class UserFeignAdapter implements UserPort {

    private final UserFeignClient userClient;

    @Override
    public boolean existsAndActive(Long userId) {
        try {
            var user = userClient.getUserById(userId);
            return user != null && user.isActive();
        } catch (feign.FeignException.NotFound e) {
            return false;
        } catch (feign.FeignException e) {
            throw new IllegalStateException("Error calling user-service: " + e.status(), e);
        }
    }
}
