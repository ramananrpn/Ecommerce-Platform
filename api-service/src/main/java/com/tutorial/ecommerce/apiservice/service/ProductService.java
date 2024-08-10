package com.tutorial.ecommerce.apiservice.service;

import com.tutorial.ecommerce.dto.ProductDto;
import com.tutorial.ecommerce.model.Product;
import com.tutorial.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDto addProduct(ProductDto productDTO) {
        Product product = new Product();
        // Convert DTO to Entity
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setCategory(productDTO.getCategory());

        product = productRepository.save(product);
        // Convert Entity to DTO
        productDTO.setId(product.getId());
        return productDTO;
    }

    public Product getProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    public Product updateProduct(Long id, ProductDto product) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStock(product.getStock());
        return productRepository.save(existingProduct);
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Page<Product> listProducts(int page, int size, String sort, String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        if (category != null && !category.isEmpty()) {
            return productRepository.findByCategory(category, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public boolean productExists(String id) {
        return productRepository.existsById(Long.valueOf(id));
    }
}

