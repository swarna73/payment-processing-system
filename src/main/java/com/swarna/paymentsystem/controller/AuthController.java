package com.swarna.paymentsystem.controller;

import com.swarna.paymentsystem.dto.LoginRequestDTO;
import com.swarna.paymentsystem.security.JwtUtil;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        try {
            // Attempt authentication; throws BadCredentialsException on failure
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            // Success → generate and return JWT
            String token = jwtUtil.generateToken(auth);
            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException ex) {
            // Failure → clean 401 + message
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password"));
        }
    }
}