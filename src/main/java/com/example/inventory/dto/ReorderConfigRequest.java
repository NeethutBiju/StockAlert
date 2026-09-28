package com.example.inventory.dto;

import jakarta.validation.constraints.*;

public record ReorderConfigRequest(
        @NotNull @PositiveOrZero Integer reorderThreshold,
        @NotNull @Positive Integer reorderQuantity) {
}