package com.example.inventory.repository;

import com.example.inventory.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    // locks the product row until the transaction ends
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}