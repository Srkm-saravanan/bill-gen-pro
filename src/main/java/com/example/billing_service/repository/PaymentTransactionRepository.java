package com.example.billing_service.repository;

import com.example.billing_service.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    // Get all split payments for a single bill (e.g., ₹5000 cash, ₹3000 UPI)
    List<PaymentTransaction> findByBill_Id(Long billId);

    // Get the entire payment history for a specific credit customer
    List<PaymentTransaction> findByCustomer_IdOrderByPaymentDateDesc(Long customerId);

    // Fetch all payments made within a date range
    List<PaymentTransaction> findByPaymentDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}