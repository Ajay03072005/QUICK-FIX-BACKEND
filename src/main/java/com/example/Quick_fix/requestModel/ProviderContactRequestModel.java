package com.example.Quick_fix.requestModel;

import lombok.Data;

@Data
public class ProviderContactRequestModel {

    
    private String firstName;

    private String lastName;

   
    private String phoneNumber;


    private String email;

    private Boolean primary;
}
