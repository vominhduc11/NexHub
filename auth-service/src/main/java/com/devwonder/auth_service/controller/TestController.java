package com.devwonder.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "TestController", description = "Authentication endpoints")
public class TestController {

    @GetMapping("/abc")
    @Operation(summary = "Test endpoint", description = "Simple test endpoint that returns 'abc'")
    @ApiResponse(responseCode = "200", description = "Successfully returned test response")
    public String abc() {
        String message = "Received request to /auth/abc";
        System.out.println(message);
        System.out.println("Current thread: " + Thread.currentThread().getName());
        return "abc";
    }
}
