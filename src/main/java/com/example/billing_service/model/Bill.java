package com.example.billing_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", unique = true, length = 50)
    private String billNumber; // e.g., SLV/2025-26/0001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer; // Can be null for quick walk-in cash sales

    @Enumerated(EnumType.STRING)
    @Column(name = "bill_type", nullable = false)
    private BillType billType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber; // Important for loading large B2B orders

    // --- Financial Summaries ---
    @Column(name = "sub_total", precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "total_discount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(name = "total_taxable_value", precision = 12, scale = 2)
    private BigDecimal totalTaxableValue;

    @Column(name = "total_cgst", precision = 12, scale = 2)
    private BigDecimal totalCgst;

    @Column(name = "total_sgst", precision = 12, scale = 2)
    private BigDecimal totalSgst;

    @Column(name = "total_igst", precision = 12, scale = 2)
    private BigDecimal totalIgst;

    @Column(name = "grand_total", precision = 12, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BillItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BillType {
        TAX_INVOICE, CASH_MEMO
    }

    public enum BillStatus {
        DRAFT, COMPLETED, CANCELLED
    }
}