package com.example.shoppingcart.infraestructure.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponseDto {

    private Long id;

    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long productId;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String name;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor que cero")
    private BigDecimal price;

    @NotNull(message = "El ID del carrito no puede ser nulo")
    private Long cartId;
}
