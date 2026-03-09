package com.example.billing_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stock_entry_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockEntryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_entry_id")
    private StockEntry stockEntry;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal quantityReceived;
    private BigDecimal purchasePricePerUnit; // Buying price
}