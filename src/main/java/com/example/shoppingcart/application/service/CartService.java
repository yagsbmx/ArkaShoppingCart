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

    @Override
    @Transactional
    public Cart createCart(Cart cart) {
        if (cart.getUserId() == null) throw new IllegalArgumentException("User ID is required");
        cart.setStatus(CartStatus.ACTIVE);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        if (cart.getItems() == null) cart.setItems(new ArrayList<>());
        BigDecimal total = cart.getItems().stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        return cartRepositoryPort.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> getCart(Long id) {
        return cartRepositoryPort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> getAllCarts() {
        return cartRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public void deleteCart(Long id) {
        Cart cart = cartRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + id));
        if (cart.getStatus() == CartStatus.COMPLETED)
            throw new IllegalStateException("Cannot delete a completed cart");
        cartRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public Cart addItem(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getStatus() != CartStatus.ACTIVE)
            throw new IllegalStateException("Cannot modify cart in status: " + cart.getStatus());

        double availableStock = productStockPort.getAvailableStock(productId);
        if (availableStock < quantity)
            throw new IllegalStateException("Not enough stock available for product " + productId);

        var productInfo = productStockPort.getProductInfo(productId);
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(productId)
                    .name(productInfo.name())
                    .price(productInfo.price())
                    .quantity(quantity)
                    .build();
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        productStockPort.decreaseStock(productId, quantity);

        BigDecimal total = cart.getItems().stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepositoryPort.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItem(Long cartId, Long productId) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getStatus() != CartStatus.ACTIVE)
            throw new IllegalStateException("Cannot modify cart in status: " + cart.getStatus());

        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        if (itemOpt.isEmpty())
            throw new IllegalArgumentException("Item not found in cart");

        CartItem item = itemOpt.get();
        cart.getItems().remove(item);
        productStockPort.increaseStock(productId, item.getQuantity());

        BigDecimal total = cart.getItems().stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepositoryPort.save(cart);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
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

        // ⬇️ Enviar clientId (el order-service habla en clientId)
        OrderFeignRequestDto orderRequest = OrderFeignRequestDto.builder()
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
            try {
                body = e.contentUTF8();
            } catch (Throwable t) {
                body = e.getMessage();
            }
            log.error("Order creation failed. status={}, body={}", e.status(), body);
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "order-service respondió " + e.status() + ": " + body,
                e
            );
        }
    }

    @Override
    @Transactional
    public BigDecimal calculateTotal(Long cartId) {
        Cart cart = cartRepositoryPort.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        BigDecimal total = cart.getItems().stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepositoryPort.save(cart);
        return total;
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void checkAbandonedCarts() {
        List<Cart> carts = cartRepositoryPort.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Cart cart : carts) {
            if (cart.getStatus() == CartStatus.ACTIVE &&
                cart.getUpdatedAt().isBefore(now.minusHours(24))) {
                for (CartItem item : cart.getItems())
                    productStockPort.increaseStock(item.getProductId(), item.getQuantity());
                cart.setStatus(CartStatus.ABANDONED);
                cart.setUpdatedAt(now);
                cartRepositoryPort.save(cart);
            }
        }
    }
}
