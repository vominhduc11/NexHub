package com.devwonder.warranty_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.warranty_service.dto.WarrantyClaimRequest;
import com.devwonder.warranty_service.dto.WarrantyClaimResponse;
import com.devwonder.warranty_service.dto.WarrantyStatsResponse;
import com.devwonder.warranty_service.service.WarrantyClaimService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/warranty/claims")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warranty Claims", description = "APIs for managing warranty claims")
public class WarrantyClaimController {
    
    private final WarrantyClaimService warrantyClaimService;
    
    @GetMapping
    @Operation(summary = "Get all warranty claims", description = "Retrieve paginated list of warranty claims")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claims retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getAllClaims(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims - page: {}, size: {}", page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.getAllClaims(page, size);
        return ResponseUtil.success("Claims retrieved successfully", claims);
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get claims by customer", description = "Retrieve warranty claims for a specific customer")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getClaimsByCustomer(
            @Parameter(description = "Customer ID", example = "1") @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/customer/{} - page: {}, size: {}", customerId, page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsByCustomer(customerId, page, size);
        return ResponseUtil.success("Customer claims retrieved successfully", claims);
    }
    
    @GetMapping("/reseller/{resellerId}")
    @Operation(summary = "Get claims by reseller", description = "Retrieve warranty claims for products sold by a reseller")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getClaimsByReseller(
            @Parameter(description = "Reseller ID", example = "1") @PathVariable Long resellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/reseller/{} - page: {}, size: {}", resellerId, page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsByReseller(resellerId, page, size);
        return ResponseUtil.success("Reseller claims retrieved successfully", claims);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get claims by status", description = "Retrieve warranty claims with specific status")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getClaimsByStatus(
            @Parameter(description = "Claim status", example = "PENDING") 
            @PathVariable WarrantyClaimResponse.ClaimStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/status/{} - page: {}, size: {}", status, page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsByStatus(status, page, size);
        return ResponseUtil.success("Claims by status retrieved successfully", claims);
    }
    
    @GetMapping("/pending")
    @Operation(summary = "Get pending claims", description = "Retrieve pending warranty claims ordered by priority")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getPendingClaims(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/pending - page: {}, size: {}", page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.getPendingClaims(page, size);
        return ResponseUtil.success("Pending claims retrieved successfully", claims);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recent claims", description = "Retrieve recently submitted warranty claims")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getRecentClaims(
            @Parameter(description = "Number of days", example = "7") @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/recent - days: {}, page: {}, size: {}", days, page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.getRecentClaims(days, page, size);
        return ResponseUtil.success("Recent claims retrieved successfully", claims);
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue claims", description = "Retrieve claims that are pending for too long")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getOverdueClaims(
            @Parameter(description = "Number of days", example = "7") @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/overdue - days: {}, page: {}, size: {}", days, page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.getOverdueClaims(days, page, size);
        return ResponseUtil.success("Overdue claims retrieved successfully", claims);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search warranty claims", description = "Search claims by issue description")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> searchClaims(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/search - keyword: '{}', page: {}, size: {}", keyword, page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.searchClaims(keyword, page, size);
        return ResponseUtil.success("Search results retrieved successfully", claims);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get claims by date range", description = "Retrieve claims submitted within date range")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getClaimsByDateRange(
            @Parameter(description = "Start date", example = "2024-01-01T00:00:00") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date", example = "2024-12-31T23:59:59") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/date-range - from: {} to: {}, page: {}, size: {}", 
                startDate, endDate, page, size);
        
        Page<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsByDateRange(startDate, endDate, page, size);
        return ResponseUtil.success("Claims by date range retrieved successfully", claims);
    }
    
    @GetMapping("/needing-attention")
    @Operation(summary = "Get claims needing attention", description = "Get high priority pending claims")
    public ResponseEntity<BaseResponse<List<WarrantyClaimResponse>>> getClaimsNeedingAttention() {
        log.info("GET /warranty/claims/needing-attention");
        
        List<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsNeedingAttention();
        return ResponseUtil.success("Claims needing attention retrieved successfully", claims);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID", description = "Retrieve a specific warranty claim")
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> getClaimById(
            @Parameter(description = "Claim ID", example = "1") @PathVariable Long id) {
        
        log.info("GET /warranty/claims/{}", id);
        
        Optional<WarrantyClaimResponse> claim = warrantyClaimService.getClaimById(id);
        if (claim.isPresent()) {
            return ResponseUtil.success("Claim retrieved successfully", claim.get());
        } else {
            return ResponseUtil.notFound("Claim not found");
        }
    }
    
    @GetMapping("/number/{claimNumber}")
    @Operation(summary = "Get claim by number", description = "Retrieve a warranty claim by claim number")
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> getClaimByNumber(
            @Parameter(description = "Claim number", example = "WC-2024-001") @PathVariable String claimNumber) {
        
        log.info("GET /warranty/claims/number/{}", claimNumber);
        
        Optional<WarrantyClaimResponse> claim = warrantyClaimService.getClaimByNumber(claimNumber);
        if (claim.isPresent()) {
            return ResponseUtil.success("Claim retrieved successfully", claim.get());
        } else {
            return ResponseUtil.notFound("Claim not found");
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get warranty statistics", description = "Get comprehensive warranty and claims statistics")
    public ResponseEntity<BaseResponse<WarrantyStatsResponse>> getWarrantyStats() {
        log.info("GET /warranty/claims/stats");
        
        WarrantyStatsResponse stats = warrantyClaimService.getWarrantyStats();
        return ResponseUtil.success("Warranty statistics retrieved successfully", stats);
    }
    
    @PostMapping
    @Operation(summary = "Create warranty claim", description = "Submit a new warranty claim")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Claim created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid claim data or warranty expired"),
        @ApiResponse(responseCode = "404", description = "Purchased product not found")
    })
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> createClaim(
            @Valid @RequestBody WarrantyClaimRequest request) {
        
        log.info("POST /warranty/claims - Creating claim for purchased product: {}", request.getPurchasedProductId());
        
        WarrantyClaimResponse response = warrantyClaimService.createClaim(request);
        return ResponseUtil.created("Warranty claim submitted successfully", response);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update claim status", description = "Update warranty claim status. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> updateClaimStatus(
            @Parameter(description = "Claim ID", example = "1") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam WarrantyClaimResponse.ClaimStatus status,
            @Parameter(description = "Internal notes") @RequestParam(required = false) String internalNotes) {
        
        log.info("PATCH /warranty/claims/{}/status - New status: {}", id, status);
        
        WarrantyClaimResponse response = warrantyClaimService.updateClaimStatus(id, status, internalNotes);
        return ResponseUtil.success("Claim status updated successfully", response);
    }
    
    @PatchMapping("/{id}/resolution")
    @Operation(summary = "Add resolution notes", description = "Add resolution notes to a warranty claim. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> addResolutionNotes(
            @Parameter(description = "Claim ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Resolution notes") @RequestParam String resolutionNotes) {
        
        log.info("PATCH /warranty/claims/{}/resolution - Adding resolution notes", id);
        
        WarrantyClaimResponse response = warrantyClaimService.addResolutionNotes(id, resolutionNotes);
        return ResponseUtil.success("Resolution notes added successfully", response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete warranty claim", description = "Delete a warranty claim. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Void>> deleteClaim(
            @Parameter(description = "Claim ID", example = "1") @PathVariable Long id) {
        
        log.info("DELETE /warranty/claims/{}", id);
        
        warrantyClaimService.deleteClaim(id);
        return ResponseUtil.successVoid("Warranty claim deleted successfully");
    }
}