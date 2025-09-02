package com.devwonder.auth_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.auth_service.dto.ResellerRegistrationRequest;
import com.devwonder.auth_service.dto.ResellerRegistrationResponse;
import com.devwonder.auth_service.service.ResellerService;
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
@RequestMapping("/auth/reseller")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reseller Management", description = "APIs for reseller account management")
public class ResellerController {
    
    private final ResellerService resellerService;
    
    @PostMapping("/register")
    @Operation(summary = "Register new reseller account", description = "Create a new reseller account with DEALER role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseller account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<BaseResponse<ResellerRegistrationResponse>> registerReseller(@Valid @RequestBody ResellerRegistrationRequest request) throws BaseException {
        log.info("Received reseller registration request for username: {}", request.getUsername());
        
        ResellerRegistrationResponse response = resellerService.registerReseller(request);
        return ResponseUtil.created("Reseller account created successfully", response);
    }
}