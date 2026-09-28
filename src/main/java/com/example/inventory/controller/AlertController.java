package com.example.inventory.controller;

import com.example.inventory.dto.AlertResponse;
import com.example.inventory.model.AlertStatus;
import com.example.inventory.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService service;

    @GetMapping
    public List<AlertResponse> list(@RequestParam(required = false) AlertStatus status) {
        return service.list(status);
    }

    @PostMapping
    public ResponseEntity<AlertResponse> raise(@RequestParam Long productId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.raiseManually(productId));
    }
}