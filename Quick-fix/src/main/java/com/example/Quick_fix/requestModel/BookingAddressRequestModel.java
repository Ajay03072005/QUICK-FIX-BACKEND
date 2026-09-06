package com.example.Quick_fix.requestModel;

import lombok.Data;

@Data
public class BookingAddressRequestModel {

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private Double latitude;

    private Double longitude;
}