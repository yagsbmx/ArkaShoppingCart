package com.example.shoppingcart.infraestructure.adapter.out.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFeignResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String productStatus;
    private Long brandId;
    private Long categoryId;
}