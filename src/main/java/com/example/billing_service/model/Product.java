package com.example.billing_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name cannot be empty")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Short code is required for fast billing")
    @Column(name = "short_code", unique = true, nullable = false, length = 20)
    private String shortCode;

    @Column(name = "category", length = 50)
    private String category; // e.g., Stainless Steel, Brass, Copper

    @NotBlank(message = "HSN Code is mandatory")
    @Column(name = "hsn_code", length = 10, nullable = false)
    private String hsnCode;

    @NotNull(message = "GST percentage is required")
    @Column(name = "gst_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal gstPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @NotNull(message = "Base rate is required")
    @Column(name = "base_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal baseRate;

    @Column(name = "current_stock", precision = 10, scale = 3) // Scale 3 for weight-based items (e.g., 1.550 kg)
    private BigDecimal currentStock;

    @Column(name = "reorder_level", precision = 10, scale = 3)
    private BigDecimal reorderLevel;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum UnitOfMeasure {
        PIECE, SET, DOZEN, KILOGRAM, PAIR, BOX
    }
}