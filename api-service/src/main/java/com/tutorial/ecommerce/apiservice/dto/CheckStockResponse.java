package com.tutorial.ecommerce.apiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckStockResponse {
    private String productId;
    private int stock;

}
