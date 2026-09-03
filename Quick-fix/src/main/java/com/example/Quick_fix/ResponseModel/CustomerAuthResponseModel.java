package com.example.Quick_fix.ResponseModel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CustomerAuthResponseModel {

    private Long id;

    private Long customerId;

    private String email;

    private boolean emailVerified;

    private LocalDateTime lastLoginAt;
    
}