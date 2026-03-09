package com.example.billing_service.controller;

import com.example.billing_service.model.PaymentTransaction;
import com.example.billing_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    // Endpoint to record how the bill was paid (Cash, UPI, Credit, etc.)
    @PostMapping
    public ResponseEntity<PaymentTransaction> recordPayment(
            @RequestParam(required = false) Long billId,
            @RequestParam(required = false) Long customerId,
            @RequestParam PaymentTransaction.PaymentMode mode,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reference) {

        PaymentTransaction payment = paymentService.recordPayment(billId, customerId, mode, amount, reference);
        return ResponseEntity.ok(payment);
    }
}