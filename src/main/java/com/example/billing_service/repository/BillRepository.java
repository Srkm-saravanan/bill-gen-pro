package com.example.billing_service.repository;

import com.example.billing_service.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    // To pull up an exact bill during a return or dispute
    Optional<Bill> findByBillNumber(String billNumber);

    // To fetch all DRAFT bills so the owner can resume them later
    List<Bill> findByStatus(Bill.BillStatus status);

    // To show a customer's past purchase history on the billing screen
    List<Bill> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);

    // Fetch all completed bills for a specific date range (like today!)
    List<Bill> findByCreatedAtBetweenAndStatus(
            LocalDateTime start,
            LocalDateTime end,
            Bill.BillStatus status
    );
}