package com.example.shoppingcart.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shoppingcart.domain.model.Cart;
import com.example.shoppingcart.domain.model.CartItem;
import com.example.shoppingcart.domain.model.enums.CartStatus;
import com.example.shoppingcart.domain.ports.in.CartUseCase;
import com.example.shoppingcart.domain.ports.out.CartRepositoryPort;
import com.example.shoppingcart.domain.ports.out.NotificationPort;
import com.example.shoppingcart.domain.ports.out.ProductStockPort;
import com.example.shoppingcart.domain.ports.out.OrderPort;
import com.example.shoppingcart.infraestructure.adapter.out.order.OrderFeignRequestDto;
import com.example.shoppingcart.infraestructure.adapter.out.order.OrderItemRequestDto;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService implements CartUseCase {

    private final CartRepositoryPort cartRepositoryPort;
    private final ProductStockPort productStockPort;
    private final OrderPort orderPort;
    private final NotificationPort notificationPort;

    @Transactional
    @Override
    public Cart createCart(Cart cart) {
        if (cart.getUserId() == null) throw new IllegalArgumentException("User ID is required");
        cart.setStatus(CartStatus.ACTIVE);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        if (cart.getItems() == null) cart.setItems(new ArrayList<>());
        cart.setTotalPrice(calcTotal(cart));
        return cartRepositoryPort.save(cart);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Cart> getCart(Long id) {
        return cartRepositoryPort.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Cart> getAllCarts() {
        return cartRepositoryPort.findAll();
    }

    @Transactional
    @Override
    public void deleteCart(Long id) {
        Cart cart = cartRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + id));
        if (cart.getStatus() == CartStatus.COMPLETED)
            throw new IllegalStateException("Cannot delete a completed cart");
        cartRepositoryPort.deleteById(id);
    }

    @Transactional
    @Override
    public Cart addItem(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getStatus() != CartStatus.ACTIVE)
            throw new IllegalStateException("Cannot modify cart in status: " + cart.getStatus());

        double availableStock = productStockPort.getAvailableStock(productId);
        if (availableStock < quantity)
            throw new IllegalStateException("Not enough stock available for product " + productId);

        var productInfo = productStockPort.getProductInfo(productId);
        var existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            var existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            var newItem = CartItem.builder()
                    .productId(productId)
                    .name(productInfo.name())
                    .price(productInfo.price())
                    .quantity(quantity)
                    .build();
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        productStockPort.decreaseStock(productId, quantity);

        cart.setTotalPrice(calcTotal(cart));
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepositoryPort.save(cart);
    }

    @Transactional
    @Override
    public Cart removeItem(Long cartId, Long productId) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getStatus() != CartStatus.ACTIVE)
            throw new IllegalStateException("Cannot modify cart in status: " + cart.getStatus());

        var itemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        if (itemOpt.isEmpty())
            throw new IllegalArgumentException("Item not found in cart");

        var item = itemOpt.get();
        cart.getItems().remove(item);
        productStockPort.increaseStock(productId, item.getQuantity());

        cart.setTotalPrice(calcTotal(cart));
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepositoryPort.save(cart);
    }

    @Transactional
    @Override
    public Cart clearCart(Long cartId) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getStatus() != CartStatus.ACTIVE)
            throw new IllegalStateException("Cannot modify cart in status: " + cart.getStatus());

        for (CartItem item : cart.getItems())
            productStockPort.increaseStock(item.getProductId(), item.getQuantity());

        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepositoryPort.save(cart);
    }

    @Transactional
    @Override
    public Cart completeCart(Long cartId) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getStatus() != CartStatus.ACTIVE)
            throw new IllegalStateException("Cannot complete cart in status: " + cart.getStatus());

        List<OrderItemRequestDto> items = cart.getItems().stream()
                .map(ci -> OrderItemRequestDto.builder()
                        .productId(ci.getProductId())
                        .quantity(ci.getQuantity())
                        .unitPrice(ci.getPrice())
                        .build())
                .toList();

        var orderRequest = OrderFeignRequestDto.builder()
                .clientId(cart.getUserId())
                .items(items)
                .build();

        try {
            orderPort.sendOrder(orderRequest);
            cart.setStatus(CartStatus.COMPLETED);
            cart.setUpdatedAt(LocalDateTime.now());
            return cartRepositoryPort.save(cart);
        } catch (FeignException e) {
            String body;
            try { body = e.contentUTF8(); } catch (Throwable t) { body = e.getMessage(); }
            log.error("Order creation failed. status={}, body={}", e.status(), body);
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "order-service respondió " + e.status() + ": " + body,
                    e
            );
        }
    }

    @Transactional
    @Override
    public BigDecimal calculateTotal(Long cartId) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        var total = calcTotal(cart);
        cart.setTotalPrice(total);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepositoryPort.save(cart);
        return total;
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    @Override
    public void checkAbandonedCarts() {
        var carts = cartRepositoryPort.findAll();
        var now = LocalDateTime.now();
        for (Cart cart : carts) {
            if (cart.getStatus() == CartStatus.ACTIVE &&
                cart.getUpdatedAt() != null &&
                cart.getUpdatedAt().isBefore(now.minusHours(24))) {
                for (CartItem item : cart.getItems())
                    productStockPort.increaseStock(item.getProductId(), item.getQuantity());
                cart.setStatus(CartStatus.ABANDONED);
                cart.setUpdatedAt(now);
                cartRepositoryPort.save(cart);
                safeNotify(cart);
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Cart> findAbandoned(long minutes) {
        var threshold = LocalDateTime.now().minusMinutes(minutes);
        return cartRepositoryPort.findAbandonedSince(threshold);
    }

    @Override
    public void notifyAbandoned(Long cartId) {
        var cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        safeNotify(cart);
    }

    private void safeNotify(Cart cart) {
        try {
            notificationPort.notifyAbandonedCart(cart, null);
        } catch (Exception ex) {
            log.warn("Notification failed for cartId={}: {}", cart.getId(), ex.getMessage());
        }
    }

    private BigDecimal calcTotal(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) return BigDecimal.ZERO;
        return cart.getItems().stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
