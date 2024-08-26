package com.tutorial.ecommerce.apiservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "User ID is mandatory, must not be empty")
    private String userId;

    @NotEmpty(message = "orders are mandatory, must not be empty")
    @Valid
    private List<ProductOrderRequest> orders;

    @NotBlank(message = "Address is mandatory, , must not be empty")
    private String address;

    @NotBlank(message = "Payment method is mandatory, , must not be empty")
    private String paymentMethod;

    @NotBlank(message = "Email is mandatory, must not be empty")
    private String email;
}
