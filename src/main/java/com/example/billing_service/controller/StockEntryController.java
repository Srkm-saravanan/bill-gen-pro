package com.example.billing_service.controller;

import com.example.billing_service.model.StockEntry;
import com.example.billing_service.service.StockEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class StockEntryController {

    private final StockEntryService stockEntryService;

    @PostMapping("/receive")
    public ResponseEntity<StockEntry> receiveShipment(@RequestBody StockEntry entry) {
        return ResponseEntity.ok(stockEntryService.receiveStock(entry));
    }
}