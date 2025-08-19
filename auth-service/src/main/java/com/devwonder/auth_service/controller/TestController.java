package com.devwonder.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class TestController {

    @GetMapping("/abc")
    public String abc(){
        return "abc";
    }
}
