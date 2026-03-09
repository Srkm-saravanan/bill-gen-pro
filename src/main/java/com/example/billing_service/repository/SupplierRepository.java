package com.example.billing_service.repository;

import com.example.billing_service.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByName(String name);

    List<Supplier> findByNameContainingIgnoreCase(String namePart);

    // ✅ Match the field name 'isActive' exactly
    List<Supplier> findByIsActiveTrue();
}