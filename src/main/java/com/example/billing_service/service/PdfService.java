package com.example.billing_service.service;

import com.example.billing_service.model.Bill;
import com.example.billing_service.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    private final BillRepository billRepository;

    public byte[] generateInvoicePdf(Long billId) {
        log.info("Generating PDF Invoice for Bill ID: {}", billId);

        try {
            // 1. Fetch the completely calculated bill from your database
            Bill bill = billRepository.findById(billId)
                    .orElseThrow(() -> new RuntimeException("Bill not found for PDF generation"));

            // 2. Load the Jasper template file (we will create this next!)
            File file = ResourceUtils.getFile("classpath:reports/invoice.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

            // 3. Map the data into the template
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("billNumber", bill.getBillNumber());
            parameters.put("shopName", "Sri Lakshmi Vessels"); // As requested by the client
            parameters.put("grandTotal", bill.getGrandTotal());

            // If there's a customer, add their details, otherwise label as Walk-in
            if (bill.getCustomer() != null) {
                parameters.put("customerName", bill.getCustomer().getName());
                parameters.put("customerPhone", bill.getCustomer().getPhoneNumber());
            } else {
                parameters.put("customerName", "Walk-in Customer");
                parameters.put("customerPhone", "N/A");
            }

            // 4. Pass the line items (the cookers, etc.) into the report
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(bill.getItems());

            // 5. Build and export the PDF to a byte array
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (FileNotFoundException | JRException e) {
            log.error("Failed to generate PDF", e);
            throw new RuntimeException("Error generating PDF invoice", e);
        }
    }
}