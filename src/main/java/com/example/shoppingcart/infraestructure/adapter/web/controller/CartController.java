package com.example.shoppingcart.infraestructure.adapter.web.controller;

import com.example.shoppingcart.domain.model.Cart;
import com.example.shoppingcart.domain.ports.in.CartUseCase;
import com.example.shoppingcart.infraestructure.dto.CartRequestDto;
import com.example.shoppingcart.infraestructure.dto.CartResponseDto;
import com.example.shoppingcart.infraestructure.mapper.CartMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartUseCase useCase;
    private final CartMapper mapper;

    @PostMapping("/create")
    public ResponseEntity<CartResponseDto> createCart(@Valid @RequestBody CartRequestDto request) {
        Cart cart = mapper.requestToDomain(request);
        Cart created = useCase.createCart(cart);
        return new ResponseEntity<>(mapper.toResponseDto(created), HttpStatus.CREATED);
    }

    @PostMapping("/complete/user/{userId}")
    public ResponseEntity<CartResponseDto> completeCartByUser(@PathVariable("userId") Long userId) {
        var cartOpt = useCase.getAllCarts().stream()
                .filter(c -> c.getUserId().equals(userId) && c.getStatus().name().equals("ACTIVE"))
                .findFirst();

        if (cartOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cart completed = useCase.completeCart(cartOpt.get().getId());
        return ResponseEntity.ok(mapper.toResponseDto(completed));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable("id") Long id) {
        return useCase.getCart(id)
                .map(cart -> ResponseEntity.ok(mapper.toResponseDto(cart)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getAllCarts() {
        List<Cart> carts = useCase.getAllCarts();
        if (carts == null || carts.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(carts.stream().map(mapper::toResponseDto).toList());
    }

    @PostMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponseDto> addItem(
            @PathVariable("cartId") Long cartId,
            @PathVariable("productId") Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        Cart updated = useCase.addItem(cartId, productId, quantity);
        return ResponseEntity.ok(mapper.toResponseDto(updated));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponseDto> removeItem(
            @PathVariable("cartId") Long cartId,
            @PathVariable("productId") Long productId) {
        Cart updated = useCase.removeItem(cartId, productId);
        return ResponseEntity.ok(mapper.toResponseDto(updated));
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<CartResponseDto> clearCart(@PathVariable("cartId") Long cartId) {
        Cart cleared = useCase.clearCart(cartId);
        return ResponseEntity.ok(mapper.toResponseDto(cleared));
    }

    @PostMapping("/{cartId}/complete")
    public ResponseEntity<CartResponseDto> completeCart(@PathVariable("cartId") Long cartId) {
        Cart completed = useCase.completeCart(cartId);
        return ResponseEntity.ok(mapper.toResponseDto(completed));
    }

    @GetMapping("/active/user/{userId}")
    public ResponseEntity<CartResponseDto> getActiveCartByUser(@PathVariable("userId") Long userId) {
        var cartOpt = useCase.getAllCarts().stream()
                .filter(c -> c.getUserId().equals(userId) && c.getStatus().name().equals("ACTIVE"))
                .findFirst();

        if (cartOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toResponseDto(cartOpt.get()));
    }

    @GetMapping("/{cartId}/total")
    public ResponseEntity<BigDecimal> calculateTotal(@PathVariable("cartId") Long cartId) {
        BigDecimal total = useCase.calculateTotal(cartId);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("cartId") Long cartId) {
        useCase.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/abandoned")
    public ResponseEntity<List<CartResponseDto>> listAbandoned(
            @RequestParam(name = "minutes", defaultValue = "60") long minutes) {
        List<Cart> carts = useCase.findAbandoned(minutes);
        if (carts == null || carts.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(carts.stream().map(mapper::toResponseDto).toList());
    }

    @PostMapping("/abandoned/{cartId}/notify")
    public ResponseEntity<Map<String, String>> notifyAbandoned(@PathVariable("cartId") Long cartId) {
        useCase.notifyAbandoned(cartId);
        return ResponseEntity.ok(Map.of("message", "Recordatorio de carrito abandonado enviado"));
    }
}
