package com.example.billing_service.service;

import com.example.billing_service.dto.DailySalesSummary;
import com.example.billing_service.model.Bill;
import com.example.billing_service.model.PaymentTransaction;
import com.example.billing_service.repository.BillRepository;
import com.example.billing_service.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final BillRepository billRepository;
    private final PaymentTransactionRepository paymentRepository;

    public DailySalesSummary getTodaySalesSummary() {
        log.info("Generating real-time Daily Sales Summary for the owner dashboard...");

        // 1. Define "Today" (From 00:00:00 to 23:59:59)
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 2. Fetch Today's Data
        List<Bill> todayBills = billRepository.findByCreatedAtBetweenAndStatus(startOfDay, endOfDay, Bill.BillStatus.COMPLETED);
        List<PaymentTransaction> todayPayments = paymentRepository.findByPaymentDateBetween(startOfDay, endOfDay);

        // 3. Calculate Bill Totals
        BigDecimal totalSales = todayBills.stream()
                .map(Bill::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Calculate Payment Breakdowns
        BigDecimal cashTotal = sumPaymentsByMode(todayPayments, PaymentTransaction.PaymentMode.CASH);
        BigDecimal upiTotal = sumPaymentsByMode(todayPayments, PaymentTransaction.PaymentMode.UPI);
        BigDecimal creditTotal = sumPaymentsByMode(todayPayments, PaymentTransaction.PaymentMode.CREDIT);

        // 5. Package and Return!
        return DailySalesSummary.builder()
                .totalSalesValue(totalSales)
                .totalBillsGenerated(todayBills.size())
                .totalCashCollected(cashTotal)
                .totalUpiCollected(upiTotal)
                .totalCreditGiven(creditTotal)
                .build();
    }

    // Helper method to sum up specific payment types
    private BigDecimal sumPaymentsByMode(List<PaymentTransaction> payments, PaymentTransaction.PaymentMode mode) {
        return payments.stream()
                .filter(p -> p.getPaymentMode() == mode)
                .map(PaymentTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}