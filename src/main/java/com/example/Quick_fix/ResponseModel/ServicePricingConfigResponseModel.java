package com.example.Quick_fix.ResponseModel;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ServicePricingConfigResponseModel {
    private Integer serviceId;
    private String serviceUniqueId;
    private String serviceName;
    private String serviceType;
    private String pricingModel;
    private BigDecimal baseServiceCharge;
    private Boolean active;
    private List<ServiceOptionGroupResponseModel> optionGroups;
    private List<ServicePackageResponseModel> packages;
}
