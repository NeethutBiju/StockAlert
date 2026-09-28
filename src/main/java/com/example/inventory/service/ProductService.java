package com.example.inventory.service;

import com.example.inventory.dto.*;
import com.example.inventory.exception.*;
import com.example.inventory.model.Product;
import com.example.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final StockMovementRepository movementRepo;
    private final ReorderAlertRepository alertRepo;

    // current stock is calculated here, never read from a column
    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getSku(), p.getName(),
                p.getReorderThreshold(), p.getReorderQuantity(),
                movementRepo.currentStock(p.getId()));
    }

    private Product find(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " not found"));
    }

    @Transactional
    public ProductResponse create(ProductRequest r) {
        if (productRepo.existsBySku(r.sku()))
            throw new BusinessRuleException("SKU '" + r.sku() + "' already exists");
        Product p = new Product();
        p.setSku(r.sku());
        p.setName(r.name());
        p.setReorderThreshold(r.reorderThreshold());
        p.setReorderQuantity(r.reorderQuantity());
        return toResponse(productRepo.save(p));
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return productRepo.findAll().stream().map(this::toResponse).toList();
    }

    // feature 2: configure threshold and reorder quantity per product
    @Transactional
    public ProductResponse updateConfig(Long id, ReorderConfigRequest r) {
        Product p = find(id);
        p.setReorderThreshold(r.reorderThreshold());
        p.setReorderQuantity(r.reorderQuantity());
        return toResponse(p);
    }

    @Transactional
    public void delete(Long id) {
        Product p = find(id);
        if (movementRepo.existsByProductId(id) || alertRepo.existsByProductId(id))
            throw new BusinessRuleException("Cannot delete product with stock history or alerts");
        productRepo.delete(p);
    }
}