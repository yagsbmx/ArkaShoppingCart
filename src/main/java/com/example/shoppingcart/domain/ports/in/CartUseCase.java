package com.example.shoppingcart.domain.ports.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.example.shoppingcart.domain.model.Cart;

public interface CartUseCase {

    Cart createCart(Cart cart);
    Optional<Cart> getCart(Long id);
    List<Cart> getAllCarts();
    void deleteCart(Long id);
    Cart addItem(Long cartId, Long productId, int quantity);
    Cart removeItem(Long cartId, Long productId);
    Cart clearCart(Long cartId);
    Cart completeCart(Long cartId);
    BigDecimal calculateTotal(Long cartId);
    void checkAbandonedCarts();
}


