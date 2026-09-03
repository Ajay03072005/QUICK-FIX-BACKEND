package com.example.Quick_fix.requestModel;

import java.time.LocalDate;

import com.example.Quick_fix.Enums.Gender;

import lombok.Data;

@Data
public class CustomerRequestModel {

    private String firstName;

    private String lastName;

    private String profileImage;

    private Gender gender;

    private LocalDate dateOfBirth;
}
