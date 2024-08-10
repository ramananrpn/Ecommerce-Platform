package com.tutorial.ecommerce.apiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotBlank
    private String userId;

    @NotEmpty
    private List<String> productIds;

    @NotBlank
    private String address;

    @NotBlank
    private String paymentMethod;

    @NotBlank
    private String email;
}
