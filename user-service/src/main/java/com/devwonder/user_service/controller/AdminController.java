package com.devwonder.user_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.user_service.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management", description = "APIs for admin management")
public class AdminController {
    
    private final AdminService adminService;

    @Operation(summary = "Check if admin exists by account ID")
    @GetMapping("/{accountId}/exists")
    public ResponseEntity<BaseResponse<Boolean>> existsById(@PathVariable Long accountId) {
        boolean exists = adminService.existsById(accountId);
        return ResponseUtil.success("Admin existence checked successfully", exists);
    }
}