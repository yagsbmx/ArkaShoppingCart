package com.example.shoppingcart.infraestructure.adapter.out.notification;

import com.example.shoppingcart.domain.model.Cart;
import com.example.shoppingcart.domain.ports.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements NotificationPort {

    private final JavaMailSender mailSender;

    @Override
    public void notifyAbandonedCart(Cart cart, String email) {
        if (email == null || email.isBlank()) return;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("arkaproyect0921@gmail.com");
        msg.setTo(email);
        msg.setSubject("Recordatorio: Carrito abandonado");
        msg.setText("Tienes productos pendientes en tu carrito #" + cart.getId());
        mailSender.send(msg);
    }
}

