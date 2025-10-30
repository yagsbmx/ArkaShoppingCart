package com.example.shoppingcart.infraestructure.mapper;

import org.springframework.stereotype.Component;

import com.example.shoppingcart.domain.model.Cart;
import com.example.shoppingcart.domain.model.CartItem;
import com.example.shoppingcart.infraestructure.adapter.persistence.entity.CartEntity;
import com.example.shoppingcart.infraestructure.adapter.persistence.entity.CartItemEntity;
import com.example.shoppingcart.infraestructure.dto.CartItemRequestDto;
import com.example.shoppingcart.infraestructure.dto.CartItemResponseDto;

@Component
public class CartItemMapper {

    public CartItem toDomain(CartItemEntity entity) {
        if (entity == null) return null;

        return CartItem.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .name(entity.getName())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .cart(entity.getCart() != null ? Cart.builder()
                        .id(entity.getCart().getId())
                        .userId(entity.getCart().getUserId())
                        .status(entity.getCart().getStatus())
                        .totalPrice(entity.getCart().getTotalPrice())
                        .createdAt(entity.getCart().getCreatedAt())
                        .updatedAt(entity.getCart().getUpdatedAt())
                        .build() : null)
                .build();
    }

    public CartItemEntity toEntity(CartItem item, CartEntity parentCart) {
        if (item == null) return null;

        CartItemEntity entity = CartItemEntity.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
        entity.setCart(parentCart);
        return entity;
    }

    public CartItem requestToDomain(CartItemRequestDto dto) {
        if (dto == null) return null;

        return CartItem.builder()
                .productId(dto.getProductId())
                .name(dto.getName())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();
    }

    public CartItemEntity toEntityFromRequest(CartItemRequestDto dto, CartEntity parentCart) {
        if (dto == null) return null;

        CartItemEntity entity = CartItemEntity.builder()
                .productId(dto.getProductId())
                .name(dto.getName())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();
        entity.setCart(parentCart);
        return entity;
    }

    public CartItemResponseDto toResponseDto(CartItem item) {
        if (item == null) return null;

        return CartItemResponseDto.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .cartId(item.getCart() != null ? item.getCart().getId() : null)
                .build();
    }
}
