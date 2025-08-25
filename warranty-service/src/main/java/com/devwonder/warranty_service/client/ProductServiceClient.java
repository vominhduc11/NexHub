package com.devwonder.warranty_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    
    @GetMapping("/api/product-serials/{id}/exists")
    Boolean productSerialExists(@PathVariable("id") Long id);
}