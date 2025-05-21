package com.swarna.paymentsystem.controller;

import com.swarna.paymentsystem.dto.CreatePaymentDto;
import com.swarna.paymentsystem.model.Payment;
import com.swarna.paymentsystem.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService payments;

    @Autowired
    public PaymentController(PaymentService payments) {
        this.payments = payments;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestBody CreatePaymentDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        Payment p = payments.createPayment(dto.getAmount(), dto.getCurrency(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @PostMapping("/{id}/capture")
    public ResponseEntity<Payment> capture(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        Payment p = payments.capturePayment(id, user);
        return ResponseEntity.ok(p);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refund(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        Payment p = payments.refundPayment(id, user);
        return ResponseEntity.ok(p);
    }

    @GetMapping
    public List<Payment> list(@AuthenticationPrincipal UserDetails user) {
        return payments.listPayments(user.getUsername());
    }
}