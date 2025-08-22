package com.devwonder.auth_service.client;

import com.devwonder.auth_service.dto.CreateResellerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/user/reseller")
public interface UserServiceClient {
    
    @PostMapping
    ResponseEntity<Object> createReseller(
        @RequestBody CreateResellerRequest request,
        @org.springframework.web.bind.annotation.RequestHeader("X-API-Key") String apiKey
    );
}