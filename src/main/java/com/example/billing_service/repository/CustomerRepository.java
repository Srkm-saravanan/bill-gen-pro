package com.example.billing_service.repository;

import com.example.billing_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Auto-fill customer details instantly when phone number is typed
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // Find all customers who have pending dues (for the Outstanding Report)
    List<Customer> findByOutstandingBalanceGreaterThan(BigDecimal amount);
}