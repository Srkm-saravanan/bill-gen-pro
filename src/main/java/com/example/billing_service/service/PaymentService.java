package com.example.billing_service.service;

import com.example.billing_service.model.Bill;
import com.example.billing_service.model.Customer;
import com.example.billing_service.model.PaymentTransaction;
import com.example.billing_service.repository.BillRepository;
import com.example.billing_service.repository.CustomerRepository;
import com.example.billing_service.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final BillRepository billRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public PaymentTransaction recordPayment(Long billId, Long customerId, PaymentTransaction.PaymentMode mode, BigDecimal amount, String reference) {
        log.info("Recording {} payment of ₹{}...", mode, amount);

        PaymentTransaction payment = PaymentTransaction.builder()
                .paymentMode(mode)
                .amount(amount)
                .transactionReference(reference)
                .build();

        // 1. Link to Bill (if this payment is for a specific new bill)
        if (billId != null) {
            Bill bill = billRepository.findById(billId)
                    .orElseThrow(() -> new RuntimeException("Bill not found"));
            payment.setBill(bill);
        }

        // 2. Link to Customer & Update Khata (Credit Ledger)
        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            payment.setCustomer(customer);

            // If they are buying on CREDIT, their outstanding balance goes UP
            if (mode == PaymentTransaction.PaymentMode.CREDIT) {
                customer.setOutstandingBalance(customer.getOutstandingBalance().add(amount));
            }
            // If they are paying via CASH/UPI/CHEQUE, their outstanding balance goes DOWN
            else {
                // Only reduce balance if they actually owe money
                if (customer.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal newBalance = customer.getOutstandingBalance().subtract(amount);
                    // Prevent balance from going negative (unless you want to track advance payments this way)
                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        newBalance = BigDecimal.ZERO;
                    }
                    customer.setOutstandingBalance(newBalance);
                }
            }
            customerRepository.save(customer);
        }

        return paymentRepository.save(payment);
    }
}