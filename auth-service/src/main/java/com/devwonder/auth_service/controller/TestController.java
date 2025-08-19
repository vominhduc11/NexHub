package com.devwonder.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class TestController {

    @GetMapping("/abc")
    public String abc(){
        String message = "Received request to /auth/abc";
        System.out.println(message);
        System.out.println("Current thread: " + Thread.currentThread().getName());
        return "abc";
    }
}
