package com.devwonder.warranty_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/user/reseller/{accountId}/exists")
    Boolean resellerExists(@PathVariable("accountId") Long accountId);
    
    @GetMapping("/api/customer/{accountId}/exists")
    Boolean customerExists(@PathVariable("accountId") Long accountId);
}