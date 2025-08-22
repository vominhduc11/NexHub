package com.devwonder.product_service.controller;

import com.devwonder.product_service.dto.ProductResponse;
import com.devwonder.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "APIs for product management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve paginated list of available products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /products - page: {}, size: {}", page, size);
        
        Page<ProductResponse> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve products filtered by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category ID or pagination parameters")
    })
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long categoryId,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /products/category/{} - page: {}, size: {}", categoryId, page, size);
        
        Page<ProductResponse> products = productService.getProductsByCategory(categoryId, page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by keyword in name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @Parameter(description = "Search keyword", example = "laptop")
            @RequestParam String keyword,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /products/search?keyword='{}' - page: {}, size: {}", keyword, page, size);
        
        Page<ProductResponse> products = productService.searchProducts(keyword, page, size);
        return ResponseEntity.ok(products);
    }
}