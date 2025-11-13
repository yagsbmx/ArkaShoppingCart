package com.example.shoppingcart.domain.ports.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.shoppingcart.domain.model.Cart;

public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findById(Long id);
    List<Cart> findAll();
    void deleteById(Long id);
    List<Cart> findAbandonedSince(LocalDateTime threshold);
}

