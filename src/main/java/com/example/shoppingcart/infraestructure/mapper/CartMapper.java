package com.example.shoppingcart.infraestructure.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.shoppingcart.domain.model.Cart;
import com.example.shoppingcart.infraestructure.adapter.persistence.entity.CartEntity;
import com.example.shoppingcart.infraestructure.dto.CartRequestDto;
import com.example.shoppingcart.infraestructure.dto.CartResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final CartItemMapper cartItemMapper;

    // Request -> Domain (solo lo que el cliente puede enviar)
    public Cart requestToDomain(CartRequestDto dto) {
        if (dto == null) return null;

        Cart cart = Cart.builder()
                .userId(dto.getUserId())
                .build();

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            cart.setItems(dto.getItems().stream()
                    .map(cartItemMapper::requestToDomain)
                    .collect(Collectors.toList()));
        }

        return cart;
    }

    // Domain -> Entity
    public CartEntity toEntity(Cart cart) {
        if (cart == null) return null;

        CartEntity entity = CartEntity.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .status(cart.getStatus())
                .totalPrice(cart.getTotalPrice())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .items(new ArrayList<>())
                .build();

        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            cart.getItems().forEach(item ->
                entity.getItems().add(cartItemMapper.toEntity(item, entity))
            );
        }

        return entity;
    }

    // Entity -> Domain
    public Cart toDomain(CartEntity entity) {
        if (entity == null) return null;

        Cart cart = Cart.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .totalPrice(entity.getTotalPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(new ArrayList<>())
                .build();

        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            cart.setItems(entity.getItems().stream()
                    .map(cartItemMapper::toDomain)
                    .collect(Collectors.toList()));
        }

        return cart;
    }

    // Domain -> Response
    public CartResponseDto toResponseDto(Cart cart) {
        if (cart == null) return null;

        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setStatus(cart.getStatus());
        dto.setTotalPrice(cart.getTotalPrice());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());
        dto.setItems(cart.getItems() != null && !cart.getItems().isEmpty()
                ? cart.getItems().stream()
                    .map(cartItemMapper::toResponseDto)
                    .collect(Collectors.toList())
                : new ArrayList<>());
        return dto;
    }
}
