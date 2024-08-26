package com.tutorial.ecommerce.apiservice.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateStockRequest {
    @Min(value = 0, message = "Stock must be equal or greater than 0")
    private Integer stock;
}
