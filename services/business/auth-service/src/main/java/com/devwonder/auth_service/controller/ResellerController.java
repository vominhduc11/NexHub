package com.devwonder.auth_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/reseller")
@Slf4j
@Tag(name = "Reseller Management", description = "DEPRECATED APIs - Use user-service instead")
public class ResellerController {
    
    @PostMapping("/register")
    @Operation(summary = "Register new reseller account", description = "DEPRECATED: Use user-service /user/reseller/register instead")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "410", description = "This endpoint is deprecated"),
        @ApiResponse(responseCode = "301", description = "Redirect to new endpoint")
    })
    @Deprecated
    public ResponseEntity<BaseResponse<String>> registerReseller() {
        log.warn("DEPRECATED endpoint /auth/reseller/register called");
        
        return ResponseUtil.error(
            "This endpoint is deprecated. Please use user-service /user/reseller/register instead",
            "DEPRECATED_ENDPOINT",
            org.springframework.http.HttpStatus.GONE
        );
    }
}