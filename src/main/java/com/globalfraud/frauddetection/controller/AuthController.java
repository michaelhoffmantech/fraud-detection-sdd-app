package com.globalfraud.frauddetection.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/validate")
    public ResponseEntity<Object> validateApiKey(@RequestHeader("X-API-Key") String apiKey) {
        return ResponseEntity.ok(new ValidationResponse(true));
    }

    private record ValidationResponse(boolean valid) {
    }
}
