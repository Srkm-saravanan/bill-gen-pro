package com.example.billing_service.service;

import com.example.billing_service.model.Product;
import com.example.billing_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates a new product.
     * We ensure the short code is always uppercase for consistency at the billing counter.
     */
    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        product.setShortCode(product.getShortCode().toUpperCase());
        return productRepository.save(product);
    }

    /**
     * Lightning-fast search by short code (e.g., "SSP3").
     * This directly solves the queue delay during peak season!
     */
    public Product getProductByShortCode(String shortCode) {
        return productRepository.findByShortCode(shortCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Product not found for code: " + shortCode));
    }

    /**
     * Auto-complete search when the cashier types part of a name.
     */
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Fetches all products so the owner can view the whole catalog.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Fetches products that are running low.
     * This is the "Shopping List" for the owner to call his suppliers.
     */
    public List<Product> getLowStockAlerts() {
        log.info("Fetching low stock alerts for inventory dashboard");
        return productRepository.findLowStockProducts();
    }
}