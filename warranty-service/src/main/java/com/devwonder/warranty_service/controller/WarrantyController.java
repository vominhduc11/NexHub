package com.devwonder.warranty_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/warranties")
@RequiredArgsConstructor
@Tag(name = "Warranty Management", description = "Operations related to warranty management")
@SecurityRequirement(name = "Gateway Request")
@SecurityRequirement(name = "JWT Authentication")
public class WarrantyController {

    @GetMapping("/health")
    @Operation(
        summary = "Health check for warranty service",
        description = "Returns the health status of the warranty service"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy"),
        @ApiResponse(responseCode = "500", description = "Service is unhealthy")
    })
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Warranty Service is running");
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get warranty by ID",
        description = "Retrieves warranty information by warranty ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Warranty found"),
        @ApiResponse(responseCode = "404", description = "Warranty not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<String> getWarranty(
        @Parameter(description = "Warranty ID", required = true)
        @PathVariable String id
    ) {
        return ResponseEntity.ok("Warranty with ID: " + id);
    }
}