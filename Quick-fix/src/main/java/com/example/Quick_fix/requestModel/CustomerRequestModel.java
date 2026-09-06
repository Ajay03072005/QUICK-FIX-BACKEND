package com.example.Quick_fix.requestModel;

import java.time.LocalDate;

import com.example.Quick_fix.Enums.CustomerStatus;
import com.example.Quick_fix.Enums.Gender;

import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class CustomerRequestModel {

    private String firstName;

    private String lastName;

    private String profileImage;
    
    private String phoneNumber;
    
    private Gender gender;

    private LocalDate dateOfBirth;
    
    @Enumerated
    private CustomerStatus status;
}
