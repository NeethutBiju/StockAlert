package com.example.inventory.repository;

import com.example.inventory.model.AlertStatus;
import com.example.inventory.model.ReorderAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ReorderAlertRepository extends JpaRepository<ReorderAlert, Long> {

    boolean existsByProductId(Long productId);

    boolean existsByProductIdAndStatus(Long productId, AlertStatus status);

    Optional<ReorderAlert> findByProductIdAndStatus(Long productId, AlertStatus status);

    List<ReorderAlert> findByStatus(AlertStatus status);
}