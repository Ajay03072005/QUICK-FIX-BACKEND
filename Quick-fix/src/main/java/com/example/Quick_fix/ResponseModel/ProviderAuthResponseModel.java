package com.example.Quick_fix.ResponseModel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProviderAuthResponseModel {

    private Long id;
    private Integer providerId;
    private String providerUniqueId;
    private String email;
    private LocalDateTime lastLoginAt;
    private String token;
}
