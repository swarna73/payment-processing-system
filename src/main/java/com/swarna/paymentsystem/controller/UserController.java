// src/main/java/com/swarna/paymentsystem/controller/UserController.java
package com.swarna.paymentsystem.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swarna.paymentsystem.model.User;
import com.swarna.paymentsystem.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        return ResponseEntity.noContent().build();  // ✅ This avoids frontend error
    }
}