package com.example.billing_service.service;

import com.example.billing_service.dto.ProfitReportDTO;
import com.example.billing_service.model.Bill;
import com.example.billing_service.model.BillItem;
import com.example.billing_service.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final BillRepository billRepository;

    public ProfitReportDTO getProfitReport(LocalDateTime start, LocalDateTime end) {
        log.info("Generating profit report from {} to {}", start, end);

        List<Bill> bills = billRepository.findByCreatedAtBetween(start, end);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Bill bill : bills) {
            if (bill.getItems() == null) continue;

            for (BillItem item : bill.getItems()) {
                if (item.getProduct() == null) continue;

                BigDecimal quantity = item.getQuantity();

                // Revenue = chargedRate (The selling price snapshot) * quantity
                BigDecimal sellingPrice = item.getChargedRate();
                if (sellingPrice != null && quantity != null) {
                    BigDecimal revenue = sellingPrice.multiply(quantity);
                    totalRevenue = totalRevenue.add(revenue);
                }

                // Cost = Purchase Price (at time of inventory entry) * quantity
                BigDecimal purchasePrice = item.getProduct().getPurchasePrice();
                if (purchasePrice != null && quantity != null) {
                    BigDecimal cost = purchasePrice.multiply(quantity);
                    totalCost = totalCost.add(cost);
                }
            }
        }

        BigDecimal netProfit = totalRevenue.subtract(totalCost);

        // Calculate Profit Percentage: (Net Profit / Total Cost) * 100
        BigDecimal profitPercentage = (totalCost.compareTo(BigDecimal.ZERO) > 0)
                ? netProfit.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return new ProfitReportDTO(totalRevenue, totalCost, netProfit, profitPercentage);
    }
}