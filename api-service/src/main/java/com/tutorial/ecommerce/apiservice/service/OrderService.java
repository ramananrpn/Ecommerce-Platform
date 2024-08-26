package com.tutorial.ecommerce.apiservice.service;

import com.tutorial.ecommerce.apiservice.dto.CreateOrderRequest;
import com.tutorial.ecommerce.apiservice.dto.NotificationRequest;
import com.tutorial.ecommerce.apiservice.dto.ProductOrderRequest;
import com.tutorial.ecommerce.apiservice.validator.OrderValidation;
import com.tutorial.ecommerce.dto.OrderDTO;
import com.tutorial.ecommerce.dto.ProductDto;
import com.tutorial.ecommerce.exception.BadRequestException;
import com.tutorial.ecommerce.exception.OrderNotFoundException;
import com.tutorial.ecommerce.model.Order;
import com.tutorial.ecommerce.model.Product;
import com.tutorial.ecommerce.model.ProductOrder;
import com.tutorial.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        List<ProductOrder> productOrders = request.getOrders().stream()
                .map(requestOrder -> new ProductOrder(requestOrder.getProductId(), requestOrder.getQuantity()))
                .collect(Collectors.toList());

        order.setProductOrders(productOrders);
        order.setAddress(request.getAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setTotal(calculateTotal(request.getOrders()));
        order.setStatus("Pending");
        order.setEmail(request.getEmail());

        // Update product quantity
        for (ProductOrder productOrder : productOrders) {
            Product product = productService.getProduct(Long.valueOf(productOrder.getProductId()));
            if (product != null) {
                int newStock = product.getStock() - productOrder.getQuantity();
                if (newStock < 0) {
                    throw new BadRequestException("Not enough stock for product with ID: " + productOrder.getProductId());
                }
                product.setStock(newStock);
                ProductDto productDto = convertToDto(product);
                productService.updateProduct(product.getId(), productDto);
            }
        }

        order = orderRepository.save(order);

        // Notify user
        sentOrderEmailNotification(order);

        return convertToDTO(order);
    }

    @Async
    protected void sentOrderEmailNotification(Order order) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setSubject("Order Confirmation");
        notificationRequest.setMessage(createOrderDetailsMessage(order));
        notificationRequest.setRecipientEmail(order.getEmail());
        notificationService.sendNotification(notificationRequest);
    }

    private String createOrderDetailsMessage(Order order) {
        StringBuilder message = new StringBuilder();
        message.append("<h1>Your order has been placed successfully!</h1>");
        message.append("<p>Order ID: ").append(order.getId()).append("</p>");

        // Add order details
        message.append("<h2>Order Details:</h2>");
        message.append("<p>Address: ").append(order.getAddress()).append("</p>");
        message.append("<p>Payment Method: ").append(order.getPaymentMethod()).append("</p>");
        message.append("<p>Total: ").append(order.getTotal()).append("</p>");

        // Add product details
        message.append("<h2>Product Details:</h2>");
        for (ProductOrder productOrder : order.getProductOrders()) {
            Product product = productService.getProduct(Long.valueOf(productOrder.getProductId()));
            message.append("<p>Product ID: ").append(product.getId()).append("</p>");
            message.append("<p>Product Name: ").append(product.getName()).append("</p>");
            message.append("<p>Quantity: ").append(productOrder.getQuantity()).append("</p>");
            message.append("<p>Price: ").append(product.getPrice()).append("</p>");
        }

        return message.toString();
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
            throw new BadRequestException("Order has already been shipped and cannot be canceled.");
        }

        order.setStatus("Cancelled");

        Order updatedOrder = orderRepository.save(order);

        OrderDTO orderDTO = convertToDTO(updatedOrder);

        return orderDTO;
    }

    private BigDecimal calculateTotal(List<ProductOrderRequest> orders) {
        // Initialize total price
        BigDecimal total = BigDecimal.ZERO;

        // Iterate through product orders and calculate total
        for (ProductOrderRequest order : orders) {
            Product product = productService.getProduct(Long.valueOf(order.getProductId()));
            if (product != null) {
                total = total.add(product.getPrice().multiply(new BigDecimal(order.getQuantity())));
            }
        }

        return total;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());

        // Convert product IDs to ProductDto
        List<ProductDto> productDtos = order.getProductOrders().stream()
                .map(productOrder -> productService.getProduct(Long.valueOf(productOrder.getProductId())))
                .map(this::convertToDto)
                .collect(Collectors.toList());

        dto.setProducts(productDtos);
        dto.setAddress(order.getAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setEmail(order.getEmail());
        return dto;
    }

    private ProductDto convertToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());
        productDto.setCategory(product.getCategory());
        return productDto;
    }
}
