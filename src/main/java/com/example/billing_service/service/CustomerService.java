package com.example.billing_service.service;

import com.example.billing_service.model.Customer;
import com.example.billing_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Registers a new customer (like a wholesale buyer or loyal retail client).
     */
    @Transactional
    public Customer createCustomer(Customer customer) {
        log.info("Registering new customer with phone: {}", customer.getPhoneNumber());
        return customerRepository.save(customer);
    }

    /**
     * The lightning-fast search by phone number for the billing counter.
     */
    public Customer getCustomerByPhone(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found with phone: " + phoneNumber));
    }

    /**
     * This is the money-maker: Gets a list of everyone who owes the shop money.
     */
    public List<Customer> getCustomersWithDues() {
        // We look for any balance greater than ZERO
        return customerRepository.findByOutstandingBalanceGreaterThan(BigDecimal.ZERO);
    }

    /**
     * Fetches the entire customer directory.
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}