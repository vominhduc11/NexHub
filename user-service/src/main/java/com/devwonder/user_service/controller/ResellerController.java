package com.devwonder.user_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.user_service.dto.CreateResellerRequest;
import com.devwonder.user_service.dto.ResellerResponse;
import com.devwonder.user_service.service.ResellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/reseller")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reseller Management", description = "APIs for managing reseller data")
public class ResellerController {
    
    private final ResellerService resellerService;
    
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

    @Operation(summary = "Check if reseller exists by account ID")
    @GetMapping("/{accountId}/exists")
    public ResponseEntity<BaseResponse<Boolean>> existsById(@PathVariable Long accountId) {
        boolean exists = resellerService.existsById(accountId);
        return ResponseUtil.success("Reseller existence checked successfully", exists);
    }
}