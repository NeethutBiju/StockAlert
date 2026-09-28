package com.example.inventory.controller;

import com.example.inventory.dto.*;
import com.example.inventory.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/stock-movements")
    public ResponseEntity<MovementResponse> record(@Valid @RequestBody MovementRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.record(r));
    }

    @GetMapping("/reports/fast-moving")
    public List<FastMovingDto> fastMoving(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit) {
        return stockService.fastMoving(from, to, limit);
    }
}