package com.tutorial.ecommerce.dto;

import com.tutorial.ecommerce.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String userId;
    private List<ProductDto> products;
    private String address;
    private String paymentMethod;
    private BigDecimal total;
    private String status;
    private String email;

}
