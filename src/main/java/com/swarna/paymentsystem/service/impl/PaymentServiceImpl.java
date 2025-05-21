package com.swarna.paymentsystem.service.impl;

import com.swarna.paymentsystem.model.Payment;
import com.swarna.paymentsystem.repository.PaymentRepository;
import com.swarna.paymentsystem.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.model.Refund;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repo;

    @Autowired
    public PaymentServiceImpl(PaymentRepository repo) {
        this.repo = repo;
    }

    @Override
    public Payment createPayment(BigDecimal amount, String currency, UserDetails user) {
        // 1) create record
        Payment p = new Payment();
        p.setUserEmail(user.getUsername());
        p.setAmount(amount);
        p.setCurrency(currency);
        p.setStatus("CREATED");
        p = repo.save(p);

        // 2) Create a Stripe PaymentIntent in “automatic capture” mode off
        try {
            PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())  // in cents
                .setCurrency(currency.toLowerCase())
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                .putMetadata("paymentId", p.getId().toString())
                .build();

            PaymentIntent intent = PaymentIntent.create(createParams);

            // 3) store the Stripe intent ID so we can capture later
            p.setStatus("AUTHORIZED");
            p.setStripeIntentId(intent.getId());
            repo.save(p);

        } catch (StripeException e) {
            // on failure, mark as FAILED
            p.setStatus("FAILED");
            repo.save(p);
            throw new RuntimeException("Stripe authorization failed", e);
        }

        return p;
    }

    @Override
    public Payment capturePayment(Long paymentId, UserDetails user) {
        Payment p = loadOwned(paymentId, user);
        if (!"AUTHORIZED".equals(p.getStatus())) {
            throw new IllegalStateException("Payment not in AUTHORIZED state");
        }

        try {
            // Capture the PaymentIntent
            PaymentIntent intent = PaymentIntent.retrieve(p.getStripeIntentId());
            PaymentIntentCaptureParams captureParams = 
                PaymentIntentCaptureParams.builder().build();
            intent.capture(captureParams);

            p.setStatus("CAPTURED");
            repo.save(p);

        } catch (StripeException e) {
            throw new RuntimeException("Stripe capture failed", e);
        }

        return p;
    }

    @Override
    public Payment refundPayment(Long paymentId, UserDetails user) {
        Payment p = loadOwned(paymentId, user);
        if (!"CAPTURED".equals(p.getStatus())) {
            throw new IllegalStateException("Only captured payments can be refunded");
        }

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(p.getStripeIntentId())
                .setAmount(p.getAmount().multiply(BigDecimal.valueOf(100)).longValue())  // full refund
                .build();
            Refund refund = Refund.create(params);

            p.setStatus("REFUNDED");
            repo.save(p);

        } catch (StripeException e) {
            throw new RuntimeException("Stripe refund failed", e);
        }

        return p;
    }

    @Override
    public List<Payment> listPayments(String userEmail) {
        return repo.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }

    private Payment loadOwned(Long id, UserDetails user) {
        Optional<Payment> op = repo.findById(id);
        if (op.isEmpty() || !op.get().getUserEmail().equals(user.getUsername())) {
            throw new IllegalArgumentException("Payment not found or not yours");
        }
        return op.get();
    }
}