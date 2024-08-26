package com.tutorial.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrder {
    @Column(name = "product_id")
    private String productId;

    @Column(name = "quantity")
    private Integer quantity;
}
