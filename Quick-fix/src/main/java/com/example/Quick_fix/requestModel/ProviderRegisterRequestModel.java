package com.example.Quick_fix.requestModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProviderRegisterRequestModel {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
