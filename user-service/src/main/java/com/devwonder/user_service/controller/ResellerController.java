package com.devwonder.user_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.user_service.dto.CreateResellerRequest;
import com.devwonder.user_service.dto.ResellerResponse;
import com.devwonder.user_service.service.ResellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/reseller")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reseller Management", description = "APIs for managing reseller data")
public class ResellerController {
    
    private final ResellerService resellerService;
    
    @GetMapping
    @Operation(summary = "Get all active resellers", description = "Get paginated list of all active resellers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved resellers list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<BaseResponse<Page<ResellerResponse>>> getAllResellers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Received request to get all resellers - page: {}, size: {}", page, size);
        
        try {
            Page<ResellerResponse> resellers = resellerService.getAllActiveResellers(page, size);
            return ResponseUtil.success("Resellers retrieved successfully", resellers);
            
        } catch (Exception e) {
            log.error("Error retrieving resellers: {}", e.getMessage());
            return ResponseUtil.error("Failed to retrieve resellers", "RESELLER_RETRIEVAL_ERROR", 
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping
    @Operation(summary = "Create reseller profile", description = "Create a new reseller profile with personal information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseller profile created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Reseller already exists")
    })
    public ResponseEntity<BaseResponse<ResellerResponse>> createReseller(@Valid @RequestBody CreateResellerRequest request) {
        log.info("Received create reseller request for account ID: {}", request.getAccountId());
        
        try {
            ResellerResponse response = resellerService.createReseller(request);
            return ResponseUtil.created("Reseller created successfully", response);
            
        } catch (BaseException e) {
            log.error("Error creating reseller: {}", e.getMessage());
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        }
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "Soft delete reseller", description = "Soft delete reseller profile by account ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseller soft deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Reseller not found"),
        @ApiResponse(responseCode = "400", description = "Reseller already deleted")
    })
    public ResponseEntity<BaseResponse<String>> deleteReseller(@PathVariable Long accountId) {
        log.info("Received delete reseller request for account ID: {}", accountId);
        
        try {
            resellerService.softDeleteReseller(accountId);
            return ResponseUtil.success("Reseller deleted successfully", "Reseller with account ID " + accountId + " has been soft deleted");
            
        } catch (BaseException e) {
            log.error("Error deleting reseller: {}", e.getMessage());
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        }
    }

    @PutMapping("/{accountId}/restore")
    @Operation(summary = "Restore soft-deleted reseller", description = "Restore a soft-deleted reseller profile by account ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseller restored successfully"),
        @ApiResponse(responseCode = "404", description = "Reseller not found"),
        @ApiResponse(responseCode = "400", description = "Reseller is not deleted")
    })
    public ResponseEntity<BaseResponse<String>> restoreReseller(@PathVariable Long accountId) {
        log.info("Received restore reseller request for account ID: {}", accountId);
        
        try {
            resellerService.restoreReseller(accountId);
            return ResponseUtil.success("Reseller restored successfully", "Reseller with account ID " + accountId + " has been restored");
            
        } catch (BaseException e) {
            log.error("Error restoring reseller: {}", e.getMessage());
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        }
    }

    @Operation(summary = "Check if reseller exists by account ID")
    @GetMapping("/{accountId}/exists")
    public ResponseEntity<BaseResponse<Boolean>> existsById(@PathVariable Long accountId) {
        boolean exists = resellerService.existsById(accountId);
        return ResponseUtil.success("Reseller existence checked successfully", exists);
    }
}