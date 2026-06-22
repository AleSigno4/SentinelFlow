package com.example.sentinelflow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sentinelflow.dto.LoginRequest;
import com.example.sentinelflow.dto.LoginResponse;
import com.example.sentinelflow.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername().equals("admin")
                && loginRequest.getPassword().equals("password")) {
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            logger.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        }
        else {
            logger.warn("Failed login attempt for user {}", loginRequest.getUsername());
            return ResponseEntity.status(401).build();
        }
    }
}
