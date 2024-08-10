package com.tutorial.ecommerce.apiservice.service;

import com.tutorial.ecommerce.apiservice.dto.CreateOrderRequest;
import com.tutorial.ecommerce.apiservice.dto.NotificationRequest;
import com.tutorial.ecommerce.apiservice.validator.OrderValidation;
import com.tutorial.ecommerce.dto.OrderDTO;
import com.tutorial.ecommerce.exception.OrderNotFoundException;
import com.tutorial.ecommerce.model.Order;
import com.tutorial.ecommerce.model.Product;
import com.tutorial.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final OrderValidation orderValidation;

    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository, ProductService productService, OrderValidation orderValidation, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.orderValidation = orderValidation;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        orderValidation.validateOrder(request);

        Order order = new Order();

        order.setUserId(request.getUserId());
        order.setProductIds(request.getProductIds());
        order.setAddress(request.getAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setTotal(calculateTotal(request.getProductIds()));
        order.setStatus("Pending");
        order.setEmail(request.getEmail());

        order = orderRepository.save(order);

        // Notify user
        sentOrderEmailNotification(order);

        return convertToDTO(order);
    }

    private void sentOrderEmailNotification(Order order) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setSubject("Order Confirmation");
        notificationRequest.setMessage("Your order has been placed successfully! Order ID: " + order.getId());
        notificationRequest.setRecipientEmail(order.getEmail());
        notificationService.sendNotification(notificationRequest);
    }

    public OrderDTO getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));
        return convertToDTO(order);
    }

    public Page<OrderDTO> listOrdersByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        return orderPage.map(this::convertToDTO);
    }

    @Transactional
    public OrderDTO cancelOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));

        if ("Shipped".equals(order.getStatus())) {
            throw new IllegalArgumentException("Order has already been shipped and cannot be canceled.");
        }

        order.setStatus("Cancelled");

        Order updatedOrder = orderRepository.save(order);
        return new OrderDTO();
    }

    private BigDecimal calculateTotal(List<String> productIds) {
        // Initialize total price
        BigDecimal total = BigDecimal.ZERO;

        // Iterate through product IDs and calculate total
        for (String productId : productIds) {
            Product product = productService.getProduct(Long.valueOf(productId));
            if (product != null) {
                total = total.add(product.getPrice());
            }
        }

        return total;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
//        dto.setProducts(order.getProductIds());
        dto.setAddress(order.getAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setEmail(order.getEmail());
        return dto;
    }
}
