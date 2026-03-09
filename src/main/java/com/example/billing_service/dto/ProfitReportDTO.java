package com.example.billing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProfitReportDTO {
    private BigDecimal totalRevenue;      // Total Sales
    private BigDecimal totalCost;         // Total Buying Cost
    private BigDecimal netProfit;         // Revenue - Cost
    private BigDecimal profitPercentage;  // (Profit/Cost) * 100
}