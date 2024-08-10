package com.tutorial.ecommerce.apiservice.validator;

import com.tutorial.ecommerce.apiservice.dto.CreateOrderRequest;
import com.tutorial.ecommerce.apiservice.service.ProductService;
import com.tutorial.ecommerce.model.Order;
import com.tutorial.ecommerce.repository.OrderRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class OrderValidation {

    private final ProductService productService;

    private final OrderRepository orderRepository;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public OrderValidation(ProductService productService, OrderRepository orderRepository) {
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    public void validateOrder(CreateOrderRequest request) {
        // Validate CreateOrderRequest using Hibernate Validator
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation errors: ");
            for (ConstraintViolation<CreateOrderRequest> violation : violations) {
                sb.append(violation.getMessage()).append(" ");
            }
            throw new IllegalArgumentException(sb.toString().trim());
        }

        // Validate that all products exist and are available
        List<String> productIds = request.getProductIds();
        productIds.forEach(productId -> {
            if (!productService.productExists(productId)) {
                throw new IllegalArgumentException("Product ID " + productId + " does not exist.");
            }
        });

        // Validate that address is not empty and has a valid format
        if (request.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }

        // Additional validations as needed
    }

    public void validateOrderUpdate(Order order, CreateOrderRequest request) {
        // Validate address
        if (request.getAddress() != null && request.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }

        // Validate payment method if provided
        if (request.getPaymentMethod() != null && request.getPaymentMethod().trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be empty.");
        }

        // Validate product IDs if provided
        if (request.getProductIds() != null) {
            List<String> productIds = request.getProductIds();
            productIds.forEach(productId -> {
                if (!productService.productExists(productId)) {
                    throw new IllegalArgumentException("Product ID " + productId + " does not exist.");
                }
            });
        }
    }
}

