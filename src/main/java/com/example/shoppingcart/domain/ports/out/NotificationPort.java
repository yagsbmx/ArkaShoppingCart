package com.example.shoppingcart.domain.ports.out;

import com.example.shoppingcart.domain.model.Cart;

public interface NotificationPort {
    void notifyAbandonedCart(Cart cart, String email);
}
