package com.swarna.paymentsystem.service;

import com.swarna.paymentsystem.model.Payment;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    /** Creates a new payment record (CREATED) and calls gateway to authorize/charge. */
    Payment createPayment(BigDecimal amount, String currency, UserDetails user);

    /** Capture an already-authorized payment. */
    Payment capturePayment(Long paymentId, UserDetails user);

    /** Refund a captured payment. */
    Payment refundPayment(Long paymentId, UserDetails user);

    /** List all payments for a user, newest first. */
    List<Payment> listPayments(String userEmail);
}