package com.example.inventory.dto;

import com.example.inventory.model.*;
import java.time.LocalDateTime;

public record AlertResponse(Long id, Long productId, String sku, AlertStatus status,
        long stockAtTrigger, int reorderQuantity, LocalDateTime createdAt, LocalDateTime fulfilledAt) {

    public static AlertResponse from(ReorderAlert a) {
        return new AlertResponse(a.getId(), a.getProduct().getId(), a.getProduct().getSku(),
                a.getStatus(), a.getStockAtTrigger(), a.getReorderQuantity(),
                a.getCreatedAt(), a.getFulfilledAt());
    }
}