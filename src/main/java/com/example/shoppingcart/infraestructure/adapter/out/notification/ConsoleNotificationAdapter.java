package com.example.shoppingcart.infraestructure.adapter.out.notification;

import com.example.shoppingcart.domain.model.Cart;
import com.example.shoppingcart.domain.ports.out.NotificationPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ConsoleNotificationAdapter implements NotificationPort {
    @Override
    public void notifyAbandonedCart(Cart cart, String email) {
        System.out.println("Notificación carrito abandonado cartId=" + cart.getId() + " email=" + email);
    }
}

