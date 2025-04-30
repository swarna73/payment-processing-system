package com.swarna.paymentsystem.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}