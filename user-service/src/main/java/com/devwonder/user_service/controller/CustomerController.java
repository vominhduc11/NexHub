package com.devwonder.user_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.user_service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management APIs")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @Operation(summary = "Check if customer exists by account ID")
    @GetMapping("/{accountId}/exists")
    public ResponseEntity<BaseResponse<Boolean>> existsById(@PathVariable Long accountId) {
        boolean exists = customerService.existsById(accountId);
        return ResponseEntity.ok(BaseResponse.success("Customer existence checked successfully", exists));
    }
}