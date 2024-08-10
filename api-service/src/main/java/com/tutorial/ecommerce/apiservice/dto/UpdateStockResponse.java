package com.tutorial.ecommerce.apiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UpdateStockResponse {
    private String productId;
    private int stock;
    private Instant updatedAt;
}
