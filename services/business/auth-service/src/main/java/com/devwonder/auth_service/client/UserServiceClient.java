package com.devwonder.auth_service.client;

import com.devwonder.auth_service.dto.CreateResellerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @PostMapping("/user/reseller")
    ResponseEntity<Object> createReseller(
        @RequestBody CreateResellerRequest request,
        @RequestHeader("X-API-Key") String apiKey
    );
}