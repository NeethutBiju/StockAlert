package com.example.inventory.service;

import com.example.inventory.dto.AlertResponse;
import com.example.inventory.exception.*;
import com.example.inventory.model.*;
import com.example.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final ReorderAlertRepository alertRepo;
    private final ProductRepository productRepo;
    private final StockMovementRepository movementRepo;

    private ReorderAlert create(Product p, long stock) {
        ReorderAlert a = new ReorderAlert();
        a.setProduct(p);
        a.setStockAtTrigger(stock);
        a.setReorderQuantity(p.getReorderQuantity()); // snapshot
        return alertRepo.save(a);
    }

    // feature 3: called automatically after a stock-reducing movement.
    // Skips silently if an alert is open, so a customer's sale is never rejected.
    @Transactional
    public void autoRaise(Product p, long stock) {
        if (!alertRepo.existsByProductIdAndStatus(p.getId(), AlertStatus.OPEN))
            create(p, stock);
    }

    // feature 4: new stock arrived, close the open alert
    @Transactional
    public void fulfillOpenAlert(Product p) {
        alertRepo.findByProductIdAndStatus(p.getId(), AlertStatus.OPEN).ifPresent(a -> {
            a.setStatus(AlertStatus.FULFILLED);
            a.setFulfilledAt(LocalDateTime.now());
        });
    }

    // manual raise: rule 2 is enforced loudly with a 409
    @Transactional
    public AlertResponse raiseManually(Long productId) {
        Product p = productRepo.findByIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + productId + " not found"));
        if (alertRepo.existsByProductIdAndStatus(productId, AlertStatus.OPEN))
            throw new BusinessRuleException("Product " + p.getSku() + " already has an open reorder alert");
        long stock = movementRepo.currentStock(productId);
        if (stock > p.getReorderThreshold())
            throw new BusinessRuleException("Stock " + stock + " is above threshold "
                    + p.getReorderThreshold() + "; alert not needed");
        return AlertResponse.from(create(p, stock));
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> list(AlertStatus status) {
        List<ReorderAlert> alerts = (status == null) ? alertRepo.findAll() : alertRepo.findByStatus(status);
        return alerts.stream().map(AlertResponse::from).toList();
    }
}