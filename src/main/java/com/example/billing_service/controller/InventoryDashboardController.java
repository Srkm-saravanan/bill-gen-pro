package com.example.billing_service.controller;

import com.example.billing_service.model.Product;
import com.example.billing_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class InventoryDashboardController {

    private final ProductService productService;

    @GetMapping("/low-stock")
    public List<Product> getLowStockItems() {
        return productService.getLowStockAlerts();
    }
}