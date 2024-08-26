package com.tutorial.ecommerce.apiservice.validator;

import com.tutorial.ecommerce.apiservice.dto.CreateOrderRequest;
import com.tutorial.ecommerce.apiservice.dto.ProductOrderRequest;
import com.tutorial.ecommerce.apiservice.service.ProductService;
import com.tutorial.ecommerce.exception.BadRequestException;
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
            throw new BadRequestException(sb.toString().trim());
        }

        // Validate that all products exist and are available
        List<ProductOrderRequest> orders = request.getOrders();
        orders.forEach(order -> {
            String productId = order.getProductId();
            if (!productService.productExists(productId)) {
                throw new BadRequestException("Product ID " + productId + " does not exist.");
            }
            if (order.getQuantity() <= 0) {
                throw new BadRequestException("Quantity for product ID " + productId + " must be positive.");
            }
        });

        // Validate that address is not empty and has a valid format
        if (request.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
    }
}

