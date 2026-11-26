package com.example.Quick_fix.ResponseModel;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ServicePackageResponseModel {
    private Long id;
    private Integer serviceId;
    private String packageName;
    private String description;
    private BigDecimal packagePrice;
    private Boolean active;
}
