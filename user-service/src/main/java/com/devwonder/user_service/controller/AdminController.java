package com.devwonder.user_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.user_service.dto.AdminResponse;
import com.devwonder.user_service.dto.CreateAdminRequest;
import com.devwonder.user_service.service.AdminService;
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
@RequestMapping("/user/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management", description = "APIs for creating admin accounts")
public class AdminController {
    
    private final AdminService adminService;
    
    @PostMapping
    @Operation(summary = "Create admin profile", description = "Create a new admin profile with account information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Admin profile created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Admin already exists")
    })
    public ResponseEntity<BaseResponse<AdminResponse>> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        log.info("Received create admin request for account ID: {}", request.getAccountId());
        
        try {
            AdminResponse response = adminService.createAdmin(request);
            return ResponseUtil.created("Admin created successfully", response);
            
        } catch (BaseException e) {
            log.error("Error creating admin: {}", e.getMessage());
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        }
    }

    @Operation(summary = "Check if admin exists by account ID")
    @GetMapping("/{accountId}/exists")
    public ResponseEntity<BaseResponse<Boolean>> existsById(@PathVariable Long accountId) {
        boolean exists = adminService.existsById(accountId);
        return ResponseUtil.success("Admin existence checked successfully", exists);
    }
}