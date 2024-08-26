package com.tutorial.ecommerce.apiservice.controller;

import com.tutorial.ecommerce.apiservice.dto.CheckStockResponse;
import com.tutorial.ecommerce.apiservice.dto.UpdateStockRequest;
import com.tutorial.ecommerce.apiservice.dto.UpdateStockResponse;
import com.tutorial.ecommerce.apiservice.service.InventoryService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CheckStockResponse> checkStock(@PathVariable("productId") String productId) {
        try {
            CheckStockResponse response = inventoryService.checkStock(productId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<UpdateStockResponse> updateStock(@PathVariable("productId") String productId,
                                                           @RequestBody UpdateStockRequest request) throws BadRequestException {
        UpdateStockResponse response = inventoryService.updateStock(productId, request);
        return ResponseEntity.ok(response);
    }
}
