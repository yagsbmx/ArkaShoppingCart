package com.example.shoppingcart.domain.ports.out;

public interface UserPort {
    boolean existsAndActive(Long userId);
}