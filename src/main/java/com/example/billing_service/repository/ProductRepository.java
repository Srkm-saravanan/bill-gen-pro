package com.example.billing_service.repository;

import com.example.billing_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // For lightning-fast counter billing using short codes
    Optional<Product> findByShortCode(String shortCode);

    // For the auto-complete search box when they type a name
    List<Product> findByNameContainingIgnoreCase(String name);

    // For filtering by categories like "Stainless Steel" or "Brass"
    List<Product> findByCategory(String category);

    // Custom query to find active products that need refilling / reordering
    @Query("SELECT p FROM Product p " +
            "WHERE p.currentStock <= p.reorderLevel " +
            "  AND p.currentStock IS NOT NULL " +
            "  AND p.reorderLevel IS NOT NULL " +
            "  AND p.isActive = true")
    List<Product> findLowStockProducts();

    // Optional: variant sorted by how critical the shortage is
    @Query("SELECT p FROM Product p " +
            "WHERE p.currentStock <= p.reorderLevel " +
            "  AND p.currentStock IS NOT NULL " +
            "  AND p.reorderLevel IS NOT NULL " +
            "  AND p.isActive = true " +
            "ORDER BY (p.reorderLevel - p.currentStock) DESC")
    List<Product> findLowStockProductsOrderByUrgencyDesc();
}