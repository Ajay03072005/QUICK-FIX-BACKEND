package com.example.Quick_fix.ResponseModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.Quick_fix.Enums.CustomerStatus;
import com.example.Quick_fix.Enums.Gender;

import lombok.Data;

@Data
public class CustomerResponseModel {

    private Long id;

    private String firstName;

    private String lastName;

    private String profileImage;

    private Gender gender;

    private CustomerStatus status;
    
    private String phoneNumber;
    
    private String uniqueId;

    private LocalDate dateOfBirth;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
