package com.example.inventory.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int reorderThreshold;

    @Column(nullable = false)
    private int reorderQuantity;

    @OneToMany(mappedBy = "product")
    private List<StockMovement> movements = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ReorderAlert> alerts = new ArrayList<>();
}