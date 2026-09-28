package com.example.inventory.dto;

public record ProductResponse(Long id, String sku, String name,
        int reorderThreshold, int reorderQuantity, long currentStock) {
}