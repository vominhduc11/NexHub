package com.devwonder.warranty_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
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
import org.springframework.http.HttpStatus;
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
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.getAllClaims(page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Claims retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving warranty claims", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving claims", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get claims by customer", description = "Retrieve warranty claims for a specific customer")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getClaimsByCustomer(
            @Parameter(description = "Customer ID", example = "1") @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/customer/{} - page: {}, size: {}", customerId, page, size);
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsByCustomer(customerId, page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Customer claims retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving customer claims", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving customer claims", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/reseller/{resellerId}")
    @Operation(summary = "Get claims by reseller", description = "Retrieve warranty claims for products sold by a reseller")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getClaimsByReseller(
            @Parameter(description = "Reseller ID", example = "1") @PathVariable Long resellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/reseller/{} - page: {}, size: {}", resellerId, page, size);
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsByReseller(resellerId, page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Reseller claims retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving reseller claims", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving reseller claims", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get claims by status", description = "Retrieve warranty claims with specific status")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getClaimsByStatus(
            @Parameter(description = "Claim status", example = "PENDING") 
            @PathVariable WarrantyClaimResponse.ClaimStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/status/{} - page: {}, size: {}", status, page, size);
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsByStatus(status, page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Claims by status retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving claims by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving claims by status", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/pending")
    @Operation(summary = "Get pending claims", description = "Retrieve pending warranty claims ordered by priority")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getPendingClaims(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/pending - page: {}, size: {}", page, size);
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.getPendingClaims(page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Pending claims retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving pending claims", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving pending claims", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recent claims", description = "Retrieve recently submitted warranty claims")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getRecentClaims(
            @Parameter(description = "Number of days", example = "7") @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/recent - days: {}, page: {}, size: {}", days, page, size);
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.getRecentClaims(days, page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Recent claims retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving recent claims", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving recent claims", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue claims", description = "Retrieve claims that are pending for too long")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> getOverdueClaims(
            @Parameter(description = "Number of days", example = "7") @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/overdue - days: {}, page: {}, size: {}", days, page, size);
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.getOverdueClaims(days, page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Overdue claims retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving overdue claims", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving overdue claims", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search warranty claims", description = "Search claims by issue description")
    public ResponseEntity<BaseResponse<Page<WarrantyClaimResponse>>> searchClaims(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /warranty/claims/search - keyword: '{}', page: {}, size: {}", keyword, page, size);
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.searchClaims(keyword, page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Search results retrieved successfully"));
        } catch (Exception e) {
            log.error("Error searching claims", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error searching claims", "INTERNAL_ERROR"));
        }
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
        
        try {
            Page<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsByDateRange(startDate, endDate, page, size);
            return ResponseEntity.ok(BaseResponse.success(claims, "Claims by date range retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving claims by date range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving claims by date range", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/needing-attention")
    @Operation(summary = "Get claims needing attention", description = "Get high priority pending claims")
    public ResponseEntity<BaseResponse<List<WarrantyClaimResponse>>> getClaimsNeedingAttention() {
        log.info("GET /warranty/claims/needing-attention");
        
        try {
            List<WarrantyClaimResponse> claims = warrantyClaimService.getClaimsNeedingAttention();
            return ResponseEntity.ok(BaseResponse.success(claims, "Claims needing attention retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving claims needing attention", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving claims needing attention", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID", description = "Retrieve a specific warranty claim")
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> getClaimById(
            @Parameter(description = "Claim ID", example = "1") @PathVariable Long id) {
        
        log.info("GET /warranty/claims/{}", id);
        
        try {
            Optional<WarrantyClaimResponse> claim = warrantyClaimService.getClaimById(id);
            if (claim.isPresent()) {
                return ResponseEntity.ok(BaseResponse.success(claim.get(), "Claim retrieved successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Claim not found", "NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("Error retrieving claim by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving claim", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/number/{claimNumber}")
    @Operation(summary = "Get claim by number", description = "Retrieve a warranty claim by claim number")
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> getClaimByNumber(
            @Parameter(description = "Claim number", example = "WC-2024-001") @PathVariable String claimNumber) {
        
        log.info("GET /warranty/claims/number/{}", claimNumber);
        
        try {
            Optional<WarrantyClaimResponse> claim = warrantyClaimService.getClaimByNumber(claimNumber);
            if (claim.isPresent()) {
                return ResponseEntity.ok(BaseResponse.success(claim.get(), "Claim retrieved successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Claim not found", "NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("Error retrieving claim by number", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving claim", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get warranty statistics", description = "Get comprehensive warranty and claims statistics")
    public ResponseEntity<BaseResponse<WarrantyStatsResponse>> getWarrantyStats() {
        log.info("GET /warranty/claims/stats");
        
        try {
            WarrantyStatsResponse stats = warrantyClaimService.getWarrantyStats();
            return ResponseEntity.ok(BaseResponse.success(stats, "Warranty statistics retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving warranty statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving statistics", "INTERNAL_ERROR"));
        }
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
        
        try {
            WarrantyClaimResponse response = warrantyClaimService.createClaim(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(response, "Warranty claim submitted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error creating warranty claim", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error creating warranty claim", "INTERNAL_ERROR"));
        }
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update claim status", description = "Update warranty claim status. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> updateClaimStatus(
            @Parameter(description = "Claim ID", example = "1") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam WarrantyClaimResponse.ClaimStatus status,
            @Parameter(description = "Internal notes") @RequestParam(required = false) String internalNotes) {
        
        log.info("PATCH /warranty/claims/{}/status - New status: {}", id, status);
        
        try {
            WarrantyClaimResponse response = warrantyClaimService.updateClaimStatus(id, status, internalNotes);
            return ResponseEntity.ok(BaseResponse.success(response, "Claim status updated successfully"));
        } catch (BaseException e) {
            log.error("Error with warranty claim operation: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Error updating claim status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error updating claim status", "INTERNAL_ERROR"));
        }
    }
    
    @PatchMapping("/{id}/resolution")
    @Operation(summary = "Add resolution notes", description = "Add resolution notes to a warranty claim. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<WarrantyClaimResponse>> addResolutionNotes(
            @Parameter(description = "Claim ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Resolution notes") @RequestParam String resolutionNotes) {
        
        log.info("PATCH /warranty/claims/{}/resolution - Adding resolution notes", id);
        
        try {
            WarrantyClaimResponse response = warrantyClaimService.addResolutionNotes(id, resolutionNotes);
            return ResponseEntity.ok(BaseResponse.success(response, "Resolution notes added successfully"));
        } catch (BaseException e) {
            log.error("Error with warranty claim operation: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Error adding resolution notes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error adding resolution notes", "INTERNAL_ERROR"));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete warranty claim", description = "Delete a warranty claim. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Void>> deleteClaim(
            @Parameter(description = "Claim ID", example = "1") @PathVariable Long id) {
        
        log.info("DELETE /warranty/claims/{}", id);
        
        try {
            warrantyClaimService.deleteClaim(id);
            return ResponseEntity.ok(BaseResponse.success("Warranty claim deleted successfully"));
        } catch (BaseException e) {
            log.error("Error with warranty claim operation: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Error deleting warranty claim", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error deleting warranty claim", "INTERNAL_ERROR"));
        }
    }
}