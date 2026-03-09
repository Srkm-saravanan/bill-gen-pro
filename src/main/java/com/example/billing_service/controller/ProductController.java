package com.example.billing_service.controller;

import com.example.billing_service.model.Product;
import com.example.billing_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allows your future frontend to talk to this API locally
public class ProductController {

    private final ProductService productService;

    // 1. Create a new product (Owner adding new stock to catalog)
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // 2. Get all products (For the main inventory screen)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // 3. Fast billing search by Short Code (e.g., GET /api/products/code/SSP3)
    @GetMapping("/code/{shortCode}")
    public ResponseEntity<Product> getProductByShortCode(@PathVariable String shortCode) {
        try {
            return ResponseEntity.ok(productService.getProductByShortCode(shortCode));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 4. Auto-complete search (e.g., GET /api/products/search?keyword=Cooker)
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProductsByName(keyword));
    }

    // 5. Low stock alerts for the owner's dashboard
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockAlerts() {
        return ResponseEntity.ok(productService.getLowStockAlerts());
    }
}