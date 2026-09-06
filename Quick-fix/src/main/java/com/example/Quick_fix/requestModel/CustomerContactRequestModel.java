package com.example.Quick_fix.requestModel;

import com.example.Quick_fix.Enums.ContactType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerContactRequestModel {

    @NotNull
    private ContactType contactType;

    @NotBlank
    private String contactValue;

    private boolean primaryContact;

    private boolean verified;
}