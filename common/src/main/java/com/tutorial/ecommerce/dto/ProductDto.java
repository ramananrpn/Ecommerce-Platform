package com.tutorial.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "stock is required")
    @Positive(message = "stock must be positive")
    private Integer stock;

    @NotBlank(message = "Category is required")
    private String category;
}
