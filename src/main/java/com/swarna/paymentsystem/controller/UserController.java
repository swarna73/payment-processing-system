// src/main/java/com/swarna/paymentsystem/controller/UserController.java
package com.swarna.paymentsystem.controller;

import java.util.List;
import java.util.Map;
import com.swarna.paymentsystem.model.AuditLog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swarna.paymentsystem.model.AuditLog;
import com.swarna.paymentsystem.model.User;
import com.swarna.paymentsystem.repository.AuditLogRepository;
import com.swarna.paymentsystem.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    public UserController(UserRepository userRepository,PasswordEncoder passwordEncoder, AuditLogRepository auditLogRepository ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
    }
    @GetMapping("/me")
    public UserDetails getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails;
    }
    
    
    @PutMapping("/update")
    public ResponseEntity<?> updateEmail(@RequestBody Map<String, String> request,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        String newEmail = request.get("email");

        // 1️⃣ Get the currently logged-in user by username
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // 2️⃣ Update the email
        user.setEmail(newEmail);
        userRepository.save(user);
        auditLogRepository.save(new AuditLog(user.getEmail(), "Updated email"));
        return ResponseEntity.noContent().build();  // ✅ This avoids frontend error
    }
    
    
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        auditLogRepository.save(new AuditLog(user.getEmail(), "Changed password"));
        return ResponseEntity.ok("Password changed successfully");
    }
    
    
    @GetMapping("/api/audit-logs")
    public ResponseEntity<?> getAuditLogs(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        List<AuditLog> logs = auditLogRepository.findByUserEmailOrderByTimestampDesc(userDetails.getUsername());
        return ResponseEntity.ok(logs);
    }
}