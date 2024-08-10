package com.tutorial.ecommerce.apiservice.controller;

import com.tutorial.ecommerce.apiservice.dto.CreateOrderRequest;
import com.tutorial.ecommerce.apiservice.service.OrderService;
import com.tutorial.ecommerce.dto.OrderDTO;
import com.tutorial.ecommerce.exception.OrderNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDTO createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable String id) throws OrderNotFoundException {
        return orderService.getOrderById(id);
    }

    @GetMapping("/users/{userId}/orders")
    public Page<OrderDTO> listOrdersForUser(@PathVariable String userId,
                                            @RequestParam int page,
                                            @RequestParam int size) {
        return orderService.listOrdersByUser(userId, page, size);
    }

    @PutMapping("/{id}/cancel")
    public OrderDTO cancelOrder(@PathVariable String id) throws OrderNotFoundException {
        return orderService.cancelOrder(id);
    }
}
