package com.example.inventory.service;

import com.example.inventory.dto.*;
import com.example.inventory.exception.*;
import com.example.inventory.model.*;
import com.example.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepo;
    private final StockMovementRepository movementRepo;
    private final AlertService alertService;

    @Transactional
    public MovementResponse record(MovementRequest req) {
        // lock the product row so movements for one product run one at a time
        Product p = productRepo.findByIdForUpdate(req.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product " + req.productId() + " not found"));

        int delta = req.reason().getSign() * req.quantity();
        long current = movementRepo.currentStock(p.getId());
        long after = current + delta;

        // rule 1: check BEFORE saving
        if (after < 0)
            throw new BusinessRuleException("Insufficient stock for " + p.getSku()
                    + ": available " + current + ", requested " + req.quantity());

        StockMovement m = new StockMovement();
        m.setProduct(p);
        m.setReason(req.reason());
        m.setQuantityChange(delta);
        m.setNote(req.note());
        movementRepo.save(m);

        if (req.reason() == MovementReason.PURCHASE) {
            alertService.fulfillOpenAlert(p);
        } else if (delta < 0 && after <= p.getReorderThreshold()) {
            alertService.autoRaise(p, after);
        }
        return new MovementResponse(m.getId(), p.getId(), m.getReason(),
                delta, after, m.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<FastMovingDto> fastMoving(LocalDate from, LocalDate to, int limit) {
        if (from.isAfter(to))
            throw new IllegalArgumentException("'from' must not be after 'to'");
        if (limit < 1)
            throw new IllegalArgumentException("'limit' must be at least 1");
        return movementRepo.fastMoving(from.atStartOfDay(), to.plusDays(1).atStartOfDay(),
                PageRequest.of(0, limit));
    }
}