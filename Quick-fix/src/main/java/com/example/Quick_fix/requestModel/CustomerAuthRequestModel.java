package com.example.Quick_fix.requestModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerAuthRequestModel {

	@NotBlank
    private String email;

	@NotBlank
    private String password;
    
}