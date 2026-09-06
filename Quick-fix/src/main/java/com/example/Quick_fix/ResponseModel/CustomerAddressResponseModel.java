package com.example.Quick_fix.ResponseModel;

import java.time.LocalDateTime;

import com.example.Quick_fix.Enums.AddressType;

import lombok.Data;

@Data
public class CustomerAddressResponseModel {

    private Long id;

    private Long customerId;

    private AddressType addressType;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private Double latitude;

    private Double longitude;
    
    private String uniqueId;

    private boolean defaultAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}