package com.example.billing_service.service;

import com.example.billing_service.dto.BillItemRequest;
import com.example.billing_service.dto.BillRequest;
import com.example.billing_service.model.Bill;
import com.example.billing_service.model.BillItem;
import com.example.billing_service.model.Customer;
import com.example.billing_service.model.Product;
import com.example.billing_service.repository.BillRepository;
import com.example.billing_service.repository.CustomerRepository;
import com.example.billing_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public Bill generateBill(BillRequest request) {
        log.info("Generating new {}...", request.getBillType());

        Bill bill = Bill.builder()
                .billType(request.getBillType())
                .status(Bill.BillStatus.COMPLETED)
                .vehicleNumber(request.getVehicleNumber())
                .items(new ArrayList<>())
                .build();

        // 1. Attach Customer if phone number is provided
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isBlank()) {
            Customer customer = customerRepository.findByPhoneNumber(request.getCustomerPhone())
                    .orElseThrow(() -> new RuntimeException("Customer not found with phone: " + request.getCustomerPhone()));
            bill.setCustomer(customer);
        }

        // 2. Initialize Running Totals
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;

        // 3. Process Each Line Item
        for (BillItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findByShortCode(itemReq.getProductShortCode().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Product not found with short code: " + itemReq.getProductShortCode()));

            BigDecimal qty = itemReq.getQuantity();
            BigDecimal chargedRate = itemReq.getChargedRate();
            BigDecimal taxableValue = qty.multiply(chargedRate);

            // 🔥 AUTO-STOCK REDUCTION - Added right after taxableValue calculation!
            if (product.getCurrentStock() != null) {
                // Check if sufficient stock is available
                if (product.getCurrentStock().compareTo(qty) < 0) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName() +
                            ". Available: " + product.getCurrentStock() + ", Requested: " + qty);
                }

                product.setCurrentStock(product.getCurrentStock().subtract(qty));
                productRepository.save(product); // Updates the DB instantly
                log.info("Stock updated for {}: {} -> {}", product.getName(),
                        product.getCurrentStock().add(qty), product.getCurrentStock());
            } else {
                log.warn("Product {} has no current stock defined!", product.getShortCode());
            }

            // Calculate exact tax splits
            BigDecimal halfGst = product.getGstPercentage().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
            BigDecimal itemCgst = taxableValue.multiply(halfGst).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal itemSgst = taxableValue.multiply(halfGst).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal itemTotal = taxableValue.add(itemCgst).add(itemSgst);

            // Build the BillItem entity to save to DB
            BillItem billItem = BillItem.builder()
                    .bill(bill)
                    .product(product)
                    .quantity(qty)
                    .originalMrp(product.getBaseRate())
                    .chargedRate(chargedRate)
                    .gstPercentage(product.getGstPercentage())
                    .taxableValue(taxableValue)
                    .cgstAmount(itemCgst)
                    .sgstAmount(itemSgst)
                    .igstAmount(BigDecimal.ZERO) // We can add cross-state IGST logic later
                    .totalAmount(itemTotal)
                    .build();

            bill.getItems().add(billItem);

            // Add to running totals
            subTotal = subTotal.add(taxableValue);
            totalCgst = totalCgst.add(itemCgst);
            totalSgst = totalSgst.add(itemSgst);
        }

        // 4. Finalize Bill Totals
        bill.setSubTotal(subTotal);
        bill.setTotalTaxableValue(subTotal);
        bill.setTotalCgst(totalCgst);
        bill.setTotalSgst(totalSgst);
        bill.setTotalIgst(BigDecimal.ZERO);

        BigDecimal grandTotal = subTotal.add(totalCgst).add(totalSgst);
        bill.setGrandTotal(grandTotal.setScale(0, RoundingMode.HALF_UP)); // Round off to nearest Rupee

        // 5. Generate Bill Number (e.g., SLV/2026/0001)
        bill = billRepository.save(bill);
        bill.setBillNumber("SLV/" + Year.now().getValue() + "/" + String.format("%04d", bill.getId()));

        return billRepository.save(bill); // Save again to update the generated number
    }
}