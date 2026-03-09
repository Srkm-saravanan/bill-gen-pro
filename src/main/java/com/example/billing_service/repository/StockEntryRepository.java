package com.example.billing_service.repository;

import com.example.billing_service.model.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {
    // You can add custom queries here later, like finding entries by supplier
}