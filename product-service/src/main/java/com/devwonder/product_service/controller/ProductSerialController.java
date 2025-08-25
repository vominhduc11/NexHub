package com.devwonder.product_service.controller;

import com.devwonder.product_service.service.ProductSerialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-serials")
@RequiredArgsConstructor
@Tag(name = "Product Serial", description = "Product Serial management APIs")
public class ProductSerialController {
    
    private final ProductSerialService productSerialService;
    
    @Operation(summary = "Check if product serial exists by ID")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        boolean exists = productSerialService.existsById(id);
        return ResponseEntity.ok(exists);
    }
}