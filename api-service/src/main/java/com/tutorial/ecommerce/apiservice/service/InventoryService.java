package com.tutorial.ecommerce.apiservice.service;

import com.tutorial.ecommerce.apiservice.dto.CheckStockResponse;
import com.tutorial.ecommerce.apiservice.dto.UpdateStockRequest;
import com.tutorial.ecommerce.apiservice.dto.UpdateStockResponse;
import com.tutorial.ecommerce.model.Product;
import com.tutorial.ecommerce.repository.ProductRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class InventoryService {

    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public CheckStockResponse checkStock(String productId) throws BadRequestException {
        if (!productId.matches("\\d+")) {
            throw new BadRequestException("Invalid product ID");
        }
        Optional<Product> productOpt = productRepository.findById(Long.valueOf(productId));
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return new CheckStockResponse(String.valueOf(product.getId()), product.getStock());
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }

    public UpdateStockResponse updateStock(String productId, UpdateStockRequest request) throws BadRequestException {
        if (!productId.matches("\\d+")) {
            throw new BadRequestException("Invalid product ID");
        }
        if (request.getStock() < 0) {
            throw new IllegalArgumentException("Stock level cannot be negative");
        }

        Product product = productRepository.findById(Long.valueOf(productId))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int newStock = request.getStock();
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock level cannot be negative");
        }

        product.setStock(newStock);
        productRepository.save(product);

        return new UpdateStockResponse(String.valueOf(product.getId()), newStock, Instant.now());
    }


    public void reduceStock(String productId, int quantity) throws BadRequestException {
        if (!productId.matches("\\d+")) {
            throw new BadRequestException("Invalid product ID");
        }
        Product product = productRepository.findById(Long.valueOf(productId))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new IllegalStateException("Stock level cannot be negative");
        }

        product.setStock(newStock);
        productRepository.save(product);
    }

    public void increaseStock(String productId, int quantity) {
        Product product = productRepository.findById(Long.valueOf(productId))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }
}
