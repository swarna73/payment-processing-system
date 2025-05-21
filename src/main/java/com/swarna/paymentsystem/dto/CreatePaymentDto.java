package com.swarna.paymentsystem.dto;

import java.math.BigDecimal;

public class CreatePaymentDto {
    private BigDecimal amount;
    private String currency;

    // Getters / setters

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}