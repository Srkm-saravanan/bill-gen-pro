package com.example.billing_service.controller;

import com.example.billing_service.dto.BillRequest;
import com.example.billing_service.model.Bill;
import com.example.billing_service.service.BillService;
import com.example.billing_service.service.PdfService;           // ← new import
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillController {

    private final BillService billService;
    private final PdfService pdfService;          // ← added field

    // The core endpoint to generate a new invoice!
    @PostMapping
    public ResponseEntity<Bill> generateBill(@RequestBody BillRequest request) {
        Bill generatedBill = billService.generateBill(request);
        return new ResponseEntity<>(generatedBill, HttpStatus.CREATED);
    }

    // New endpoint: Download PDF invoice
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        byte[] pdfBytes = pdfService.generateInvoicePdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}