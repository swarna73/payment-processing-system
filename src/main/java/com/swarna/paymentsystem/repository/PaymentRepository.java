package com.swarna.paymentsystem.repository;

import com.swarna.paymentsystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}