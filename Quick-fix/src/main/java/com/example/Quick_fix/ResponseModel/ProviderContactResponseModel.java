package com.example.Quick_fix.ResponseModel;

import lombok.Data;

@Data
public class ProviderContactResponseModel {

    private String firstName;

    private String lastName;

   
    private String phoneNumber;


    private String email;

    private Boolean primary;
}
