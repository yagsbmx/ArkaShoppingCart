package com.example.shoppingcart.domain.ports.in;

import java.util.List;
import com.example.shoppingcart.domain.model.CartItem;

public interface CartItemUseCase {

    CartItem getItem(Long cartId, Long productId);
    List<CartItem> getItemsByCart(Long cartId);
    CartItem updateQuantity(Long cartId, Long productId, int quantity);
    void deleteItem(Long cartId, Long productId);
}
