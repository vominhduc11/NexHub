package com.devwonder.warranty_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.warranty_service.dto.PurchasedProductRequest;
import com.devwonder.warranty_service.dto.PurchasedProductResponse;
import com.devwonder.warranty_service.service.PurchasedProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/warranty/purchased-products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Purchased Products", description = "APIs for managing purchased product warranties")
public class PurchasedProductController {
    
    private final PurchasedProductService purchasedProductService;
    
    @GetMapping
    @Operation(summary = "Get all purchased products", description = "Retrieve paginated list of purchased products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<BaseResponse<Page<PurchasedProductResponse>>> getAllPurchasedProducts(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) throws BaseException {
        
        log.info("GET /warranty/purchased-products - page: {}, size: {}", page, size);
        
        Page<PurchasedProductResponse> products = purchasedProductService.getAllPurchasedProducts(page, size);
        return ResponseUtil.success("Products retrieved successfully", products);
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get purchased products by customer", description = "Retrieve purchased products for a specific customer")
    public ResponseEntity<BaseResponse<Page<PurchasedProductResponse>>> getPurchasedProductsByCustomer(
            @Parameter(description = "Customer ID", example = "1") @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BaseException {
        
        log.info("GET /warranty/purchased-products/customer/{} - page: {}, size: {}", customerId, page, size);
        
        Page<PurchasedProductResponse> products = purchasedProductService.getPurchasedProductsByCustomer(customerId, page, size);
        return ResponseUtil.success("Customer products retrieved successfully", products);
    }
    
    @GetMapping("/reseller/{resellerId}")
    @Operation(summary = "Get purchased products by reseller", description = "Retrieve products sold by a specific reseller")
    public ResponseEntity<BaseResponse<Page<PurchasedProductResponse>>> getPurchasedProductsByReseller(
            @Parameter(description = "Reseller ID", example = "1") @PathVariable Long resellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BaseException {
        
        log.info("GET /warranty/purchased-products/reseller/{} - page: {}, size: {}", resellerId, page, size);
        
        Page<PurchasedProductResponse> products = purchasedProductService.getPurchasedProductsByReseller(resellerId, page, size);
        return ResponseUtil.success("Reseller products retrieved successfully", products);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active warranties", description = "Retrieve products with active warranties")
    public ResponseEntity<BaseResponse<Page<PurchasedProductResponse>>> getActiveWarranties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BaseException {
        
        log.info("GET /warranty/purchased-products/active - page: {}, size: {}", page, size);
        
        Page<PurchasedProductResponse> products = purchasedProductService.getActiveWarranties(page, size);
        return ResponseUtil.success("Active warranties retrieved successfully", products);
    }
    
    @GetMapping("/expired")
    @Operation(summary = "Get expired warranties", description = "Retrieve products with expired warranties")
    public ResponseEntity<BaseResponse<Page<PurchasedProductResponse>>> getExpiredWarranties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BaseException {
        
        log.info("GET /warranty/purchased-products/expired - page: {}, size: {}", page, size);
        
        Page<PurchasedProductResponse> products = purchasedProductService.getExpiredWarranties(page, size);
        return ResponseUtil.success("Expired warranties retrieved successfully", products);
    }
    
    @GetMapping("/expiring-soon")
    @Operation(summary = "Get warranties expiring soon", description = "Retrieve warranties expiring within specified days")
    public ResponseEntity<BaseResponse<Page<PurchasedProductResponse>>> getWarrantiesExpiringSoon(
            @Parameter(description = "Number of days", example = "30") @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BaseException {
        
        log.info("GET /warranty/purchased-products/expiring-soon - days: {}, page: {}, size: {}", days, page, size);
        
        Page<PurchasedProductResponse> products = purchasedProductService.getWarrantiesExpiringSoon(days, page, size);
        return ResponseUtil.success("Expiring warranties retrieved successfully", products);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get warranties by purchase date range", description = "Retrieve warranties purchased within date range")
    public ResponseEntity<BaseResponse<Page<PurchasedProductResponse>>> getWarrantiesByDateRange(
            @Parameter(description = "Start date", example = "2024-01-01") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-12-31") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BaseException {
        
        log.info("GET /warranty/purchased-products/date-range - from: {} to: {}, page: {}, size: {}", 
                startDate, endDate, page, size);
        
        Page<PurchasedProductResponse> products = purchasedProductService.getWarrantiesByDateRange(startDate, endDate, page, size);
        return ResponseUtil.success("Warranties by date range retrieved successfully", products);
    }
    
    @GetMapping("/customer/{customerId}/expiring-soon")
    @Operation(summary = "Get customer warranties expiring soon", description = "Get specific customer warranties expiring within days (for notifications)")
    public ResponseEntity<BaseResponse<List<PurchasedProductResponse>>> getCustomerWarrantiesExpiringSoon(
            @Parameter(description = "Customer ID", example = "1") @PathVariable Long customerId,
            @Parameter(description = "Number of days", example = "30") @RequestParam(defaultValue = "30") int days) throws BaseException {
        
        log.info("GET /warranty/purchased-products/customer/{}/expiring-soon - days: {}", customerId, days);
        
        List<PurchasedProductResponse> products = purchasedProductService.getCustomerWarrantiesExpiringSoon(customerId, days);
        return ResponseUtil.success("Customer expiring warranties retrieved successfully", products);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get purchased product by ID", description = "Retrieve a specific purchased product")
    public ResponseEntity<BaseResponse<PurchasedProductResponse>> getPurchasedProductById(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) throws BaseException {
        
        log.info("GET /warranty/purchased-products/{}", id);
        
        Optional<PurchasedProductResponse> product = purchasedProductService.getPurchasedProductById(id);
        if (product.isPresent()) {
            return ResponseUtil.success("Product retrieved successfully", product.get());
        } else {
            return ResponseUtil.notFound("Product not found");
        }
    }
    
    @GetMapping("/verify")
    @Operation(summary = "Verify warranty", description = "Verify warranty by product serial and customer")
    public ResponseEntity<BaseResponse<PurchasedProductResponse>> verifyWarranty(
            @Parameter(description = "Product Serial ID", example = "1") @RequestParam Long productSerialId,
            @Parameter(description = "Customer ID", example = "1") @RequestParam Long customerId) throws BaseException {
        
        log.info("GET /warranty/purchased-products/verify - productSerialId: {}, customerId: {}", productSerialId, customerId);
        
        Optional<PurchasedProductResponse> product = purchasedProductService.verifyWarranty(productSerialId, customerId);
        if (product.isPresent()) {
            return ResponseUtil.success("Warranty verified successfully", product.get());
        } else {
            return ResponseUtil.notFound("No warranty found for the specified product and customer");
        }
    }
    
    @PostMapping
    @Operation(summary = "Register new purchased product", description = "Register a new purchased product warranty")
    public ResponseEntity<BaseResponse<PurchasedProductResponse>> registerPurchasedProduct(
            @Valid @RequestBody PurchasedProductRequest request) throws BaseException {
        
        log.info("POST /warranty/purchased-products - Customer: {}", request.getIdCustomer());
        
        PurchasedProductResponse response = purchasedProductService.registerPurchasedProduct(request);
        return ResponseUtil.created("Product warranty registered successfully", response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update purchased product", description = "Update an existing purchased product warranty")
    public ResponseEntity<BaseResponse<PurchasedProductResponse>> updatePurchasedProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody PurchasedProductRequest request) throws BaseException {
        
        log.info("PUT /warranty/purchased-products/{} - Updating product", id);
        
        PurchasedProductResponse response = purchasedProductService.updatePurchasedProduct(id, request);
        return ResponseUtil.success("Product warranty updated successfully", response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete purchased product", description = "Delete a purchased product warranty. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Void>> deletePurchasedProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) throws BaseException {
        
        log.info("DELETE /warranty/purchased-products/{}", id);
        
        purchasedProductService.deletePurchasedProduct(id);
        return ResponseUtil.successVoid("Product warranty deleted successfully");
    }
    
    @PatchMapping("/{id}/update-warranty-days")
    @Operation(summary = "Update warranty remaining days", description = "Recalculate warranty remaining days for a product")
    public ResponseEntity<BaseResponse<Void>> updateWarrantyRemainingDays(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) throws BaseException {
        
        log.info("PATCH /warranty/purchased-products/{}/update-warranty-days", id);
        
        purchasedProductService.updateWarrantyRemainingDays(id);
        return ResponseUtil.successVoid("Warranty days updated successfully");
    }
}