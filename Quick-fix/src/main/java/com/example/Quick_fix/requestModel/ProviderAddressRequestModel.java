package com.example.Quick_fix.requestModel;

import lombok.Data;

@Data
public class ProviderAddressRequestModel {

    private String addressType;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private Boolean primary;
}
