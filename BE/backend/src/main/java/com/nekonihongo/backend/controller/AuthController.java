// src/main/java/com/nekonihongo/backend/controller/AuthController.java
package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.AuthRequest;
import com.nekonihongo.backend.dto.AuthResponse;
import com.nekonihongo.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

}