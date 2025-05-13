// com.swarna.paymentsystem.model.AuditLog.java

package com.swarna.paymentsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String action;
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String userEmail, String action) {
        this.userEmail = userEmail;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
}