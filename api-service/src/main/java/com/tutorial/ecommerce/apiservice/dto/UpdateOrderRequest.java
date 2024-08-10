package com.tutorial.ecommerce.apiservice.dto;

import lombok.Data;

@Data
public class UpdateOrderRequest {
    private String address;
    private String paymentMethod;
}
