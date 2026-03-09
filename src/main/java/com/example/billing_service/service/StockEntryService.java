package com.example.billing_service.service;

import com.example.billing_service.model.Product;
import com.example.billing_service.model.StockEntry;
import com.example.billing_service.model.StockEntryItem;
import com.example.billing_service.repository.ProductRepository;
import com.example.billing_service.repository.StockEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockEntryService {

    private final StockEntryRepository stockEntryRepository;
    private final ProductRepository productRepository;

    /**
     * Processes an incoming shipment (GRN).
     * 1. Saves the Stock Entry record.
     * 2. Mathematically updates the current_stock for every product received.
     */
    @Transactional
    public StockEntry receiveStock(StockEntry entry) {
        log.info("Processing stock entry from supplier: {}", entry.getSupplier().getName());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (StockEntryItem item : entry.getItems()) {
            // 1. Link item to the main entry
            item.setStockEntry(entry);

            // 2. Update Product Stock
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));

            BigDecimal newStock = product.getCurrentStock().add(item.getQuantityReceived());
            product.setCurrentStock(newStock);

            // 3. Update the last purchase price (Good for margin tracking)
            product.setPurchasePrice(item.getPurchasePricePerUnit());

            productRepository.save(product);

            // 4. Sum up the total invoice value
            BigDecimal itemTotal = item.getQuantityReceived().multiply(item.getPurchasePricePerUnit());
            totalAmount = totalAmount.add(itemTotal);
        }

        entry.setTotalPurchaseAmount(totalAmount);
        return stockEntryRepository.save(entry);
    }
}