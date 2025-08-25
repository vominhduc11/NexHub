package com.devwonder.user_service.controller;

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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ResellerResponse> createReseller(@Valid @RequestBody CreateResellerRequest request) {
        log.info("Received create reseller request for account ID: {}", request.getAccountId());
        
        try {
            ResellerResponse response = resellerService.createReseller(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            log.error("Error creating reseller: {}", e.getMessage());
            
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Check if reseller exists by account ID")
    @GetMapping("/{accountId}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long accountId) {
        boolean exists = resellerService.existsById(accountId);
        return ResponseEntity.ok(exists);
    }
}