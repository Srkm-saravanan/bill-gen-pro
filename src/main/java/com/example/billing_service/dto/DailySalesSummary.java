package com.example.billing_service.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DailySalesSummary {
    private BigDecimal totalSalesValue;
    private int totalBillsGenerated;
    private BigDecimal totalCashCollected;
    private BigDecimal totalUpiCollected;
    private BigDecimal totalCreditGiven; // How much went into the Khata today
}