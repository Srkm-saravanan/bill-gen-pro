package com.example.billing_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillItemRequest {
    private String productShortCode; // e.g., "SSP3"
    private BigDecimal quantity;
    private BigDecimal chargedRate;  // The owner's manual override price!
}