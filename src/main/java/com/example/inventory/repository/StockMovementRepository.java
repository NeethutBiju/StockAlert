package com.example.inventory.repository;

import com.example.inventory.dto.FastMovingDto;
import com.example.inventory.model.StockMovement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    boolean existsByProductId(Long productId);

    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

    // Rule 1: stock is always derived from movements, never stored
    @Query("select coalesce(sum(m.quantityChange), 0) from StockMovement m where m.product.id = :productId")
    long currentStock(@Param("productId") Long productId);

    @Query("""
            select new com.example.inventory.dto.FastMovingDto(p.id, p.sku, p.name, sum(-m.quantityChange))
            from StockMovement m join m.product p
            where m.reason = com.example.inventory.model.MovementReason.SALE
              and m.createdAt >= :startTime and m.createdAt < :endTime
            group by p.id, p.sku, p.name
            order by sum(-m.quantityChange) desc
            """)
    List<FastMovingDto> fastMoving(@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
}