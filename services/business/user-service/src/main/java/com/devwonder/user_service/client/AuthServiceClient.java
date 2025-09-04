package com.devwonder.user_service.client;

import com.devwonder.user_service.dto.CreateAccountRequest;
import com.devwonder.user_service.dto.CreateAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    
    @PostMapping("/auth/account")
    ResponseEntity<CreateAccountResponse> createAccount(
        @RequestBody CreateAccountRequest request,
        @RequestHeader("X-API-Key") String apiKey
    );
    
    @DeleteMapping("/auth/account/{accountId}")
    ResponseEntity<Void> deleteAccount(
        @PathVariable("accountId") Long accountId,
        @RequestHeader("X-API-Key") String apiKey
    );
}