package com.example.inventory.dto;

import com.example.inventory.model.MovementReason;
import jakarta.validation.constraints.*;

public record MovementRequest(
        @NotNull Long productId,
        @NotNull MovementReason reason,
        @NotNull @Positive Integer quantity,
        String note) {
}