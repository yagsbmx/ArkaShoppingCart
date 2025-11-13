package com.example.shoppingcart.infraestructure.adapter.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.shoppingcart.domain.model.Cart;
import com.example.shoppingcart.domain.ports.out.CartRepositoryPort;
import com.example.shoppingcart.infraestructure.adapter.persistence.repository.cartjpa.CartJpaRepository;
import com.example.shoppingcart.infraestructure.mapper.CartMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartPersistenceAdapter implements CartRepositoryPort {

    private final CartJpaRepository cartJpaRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public Cart save(Cart cart) {
        var entity = cartMapper.toEntity(cart);
        var saved = cartJpaRepository.save(entity);
        var reloaded = cartJpaRepository.findByIdWithItems(saved.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found after save"));
        return cartMapper.toDomain(reloaded);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findById(Long id) {
        return cartJpaRepository.findByIdWithItems(id).map(cartMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> findAll() {
        return cartJpaRepository.findAllWithItems().stream()
                .map(cartMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cartJpaRepository.deleteById(id);
    }

    @Override
    public List<Cart> findAbandonedSince(LocalDateTime threshold) {
        return cartJpaRepository.findAbandonedSince(threshold).stream()
                .map(cartMapper::toDomain)
                .toList();
    }
}
