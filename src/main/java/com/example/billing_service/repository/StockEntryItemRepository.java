package com.example.billing_service.repository;

import com.example.billing_service.model.StockEntryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockEntryItemRepository extends JpaRepository<StockEntryItem, Long> {
}