package com.example.Quick_fix.ResponseModel;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ServiceOptionResponseModel {
    private Long id;
    private Long groupId;
    private String optionName;
    private BigDecimal additionalCharge;
    private Boolean active;
}
