package com.example.inventory.dto;

import com.example.inventory.model.MovementReason;
import java.time.LocalDateTime;

public record MovementResponse(Long id, Long productId, MovementReason reason,
        int quantityChange, long stockAfter, LocalDateTime createdAt) {
}