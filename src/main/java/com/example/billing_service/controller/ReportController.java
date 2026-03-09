package com.example.billing_service.controller;

import com.example.billing_service.dto.DailySalesSummary;
import com.example.billing_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    // The endpoint that will let the owner sleep peacefully at night!
    @GetMapping("/daily-summary")
    public ResponseEntity<DailySalesSummary> getTodaySalesSummary() {
        return ResponseEntity.ok(reportService.getTodaySalesSummary());
    }
}