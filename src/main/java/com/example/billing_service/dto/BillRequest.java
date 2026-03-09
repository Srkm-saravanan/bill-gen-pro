package com.example.billing_service.dto;

import com.example.billing_service.model.Bill;
import lombok.Data;
import java.util.List;

@Data
public class BillRequest {
    private String customerPhone; // Optional (leave blank for fast walk-in sales)
    private String vehicleNumber;
    private Bill.BillType billType; // TAX_INVOICE or CASH_MEMO
    private List<BillItemRequest> items;
}