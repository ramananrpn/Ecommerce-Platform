package com.tutorial.ecommerce.apiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductOrderRequest {
    @NotBlank(message = "Product ID is mandatory, must not be empty")
    private String productId;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
