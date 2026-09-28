package com.example.inventory.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReorderAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status = AlertStatus.OPEN;

    private long stockAtTrigger;
    private int reorderQuantity; // snapshot of the config at trigger time

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime fulfilledAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}