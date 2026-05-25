package com.taskmanager.controller;

import com.taskmanager.dto.*;
import com.taskmanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, Login, Logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO requestDTO) {
        return new ResponseEntity<>(authService.register(requestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT token")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDTO) {
        return ResponseEntity.ok(authService.login(requestDTO));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate JWT token")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }
}