package com.example.Quick_fix.ResponseModel;

import com.example.Quick_fix.Enums.ServiceType;

import lombok.Data;

@Data
public class ServiceResponseModel {
    private Integer id;
    private String uniqueId;
    private ServiceType serviceType;
    private String description;
    private Double serviceCharge;
    private Boolean active;
}
