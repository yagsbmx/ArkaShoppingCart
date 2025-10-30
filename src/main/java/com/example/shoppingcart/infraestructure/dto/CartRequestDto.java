package com.example.shoppingcart.infraestructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CartRequestDto {
    @NotNull
    private Long userId;

    @Valid
    @NotEmpty
    private List<CartItemRequestDto> items;
}

