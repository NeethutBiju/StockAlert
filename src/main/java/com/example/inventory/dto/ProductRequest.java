package com.example.inventory.dto;

import jakarta.validation.constraints.*;

public record ProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotNull @PositiveOrZero Integer reorderThreshold,
        @NotNull @Positive Integer reorderQuantity) {
}