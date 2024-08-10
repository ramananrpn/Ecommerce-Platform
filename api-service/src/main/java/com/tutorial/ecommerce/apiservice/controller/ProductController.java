package com.tutorial.ecommerce.apiservice.controller;

import com.tutorial.ecommerce.apiservice.service.ProductService;
import com.tutorial.ecommerce.dto.ProductDto;
import com.tutorial.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto productDTO) {
        return productService.addProduct(productDTO);
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductDto product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? "Product deleted successfully." : "Product not found.";
    }

    @GetMapping
    public Page<Product> listProducts(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort,
            @RequestParam(required = false) String category) {
        return productService.listProducts(page, size, sort, category);
    }
}
