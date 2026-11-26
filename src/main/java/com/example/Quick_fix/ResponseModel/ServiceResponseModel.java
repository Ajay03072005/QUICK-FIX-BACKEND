package com.example.Quick_fix.ResponseModel;

import com.example.Quick_fix.Enums.ServiceType;

import lombok.Data;

@Data
public class ServiceResponseModel {
    private Integer id;
    private String uniqueId;
    private String serviceType;
    private String pricingModel;
    private String description;
    private Double serviceCharge;
    private Boolean active;
}
