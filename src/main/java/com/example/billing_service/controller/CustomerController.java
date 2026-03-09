package com.example.billing_service.controller;

import com.example.billing_service.model.Customer;
import com.example.billing_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    // 1. Create a new customer profile
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    // 2. Search for a customer instantly during billing
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Customer> getCustomerByPhone(@PathVariable String phoneNumber) {
        try {
            return ResponseEntity.ok(customerService.getCustomerByPhone(phoneNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if not found
        }
    }

    // 3. The "Who Owes Me Money?" Report Endpoint
    @GetMapping("/dues")
    public ResponseEntity<List<Customer>> getCustomersWithDues() {
        return ResponseEntity.ok(customerService.getCustomersWithDues());
    }

    // 4. Get all customers
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
}